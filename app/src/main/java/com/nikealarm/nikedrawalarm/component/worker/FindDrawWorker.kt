package com.nikealarm.nikedrawalarm.component.worker

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.hilt.Assisted
import androidx.hilt.work.WorkerInject
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.nikealarm.nikedrawalarm.database.*
import com.nikealarm.nikedrawalarm.other.Contents
import com.nikealarm.nikedrawalarm.other.NotificationBuilder
import com.nikealarm.nikedrawalarm.ui.MainActivity
import com.squareup.picasso.Picasso
import org.jsoup.Jsoup

/* 알림 울리는지 확인해보기 */
class FindDrawWorker @WorkerInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    val mDao: Dao
) : Worker(
    context,
    workerParams
) {
    private val mContext = context

    private val allShoesList = mutableListOf<ShoesDataModel>()

    override fun doWork(): Result {
        parseData()

        return Result.success()
    }

    // 크롤링
    private fun parseData() {
        parseReleasedData()
        parseSpecialData()

        checkSpecialData()
    }

    // FEED 파싱
    private fun parseReleasedData() {
        val url = "https://www.nike.com/kr/launch/"
        val doc = Jsoup.connect(url) // nike UPCOMING 창을 읽어옴
            .userAgent("19.0.1.84.52")
            .get()
        val elementsData = doc.select("li.launch-list-item")

        for (elementData in elementsData) {
            val shoesInfo = elementData.select("div.info-sect") // 신발 정보
                .select("div.btn-box")
                .select("span")
                .text()

            if (shoesInfo == "LEARN MORE") {
                continue
            }

            val shoesSubTitle = elementData.select("div.text-box")
                .select("p.txt-subject")
                .text()
            val shoesTitle = elementData.select("div.text-box")
                .select("p.txt-description")
                .text()
            val innerUrl = "https://www.nike.com" + elementData.select("a")
                .attr("href") // 해당 draw 링크창을 읽어옴

            // draw가 없을 시
            if (!mDao.existsShoesData(shoesTitle, shoesSubTitle, innerUrl)) {
                if (shoesInfo == "THE DRAW 진행예정") {
                    val innerDoc = Jsoup.connect(innerUrl)
                        .userAgent("19.0.1.84.52")
                        .get()

                    // 신발 정보를 가져옴
                    val shoesPrice = "가격 : " + innerDoc.select("div.price") // draw 신발 가격
                        .text()
                    val shoesImageUrl = innerDoc.select("li.uk-width-1-2") // draw 신발 이미지
                        .select("img")
                        .eq(0)
                        .attr("src")

                    val innerElementData = innerDoc.select("span.uk-text-bold")

                    var howToEvent = "" // 이벤트 참여방법
                    for (j in 0..2) {
                        howToEvent += innerElementData.select("p")
                            .eq(j)
                            .text() + "\n"
                    }

                    howToEvent += shoesPrice

                    val shoesData =
                        ShoesDataModel(
                            null,
                            shoesSubTitle,
                            shoesTitle,
                            howToEvent,
                            shoesImageUrl,
                            innerUrl,
                            ShoesDataModel.CATEGORY_DRAW
                        )

                    insertShoesData(shoesData)
                }
            }

            allShoesList.add(ShoesDataModel(0, shoesSubTitle, shoesTitle, null, null, innerUrl))
        }
    }

    // UPCOMING 파싱
    private fun parseSpecialData() {
        val url = "https://www.nike.com/kr/launch/?type=upcoming&activeDate=date-filter:AFTER"
        val doc = Jsoup.connect(url) // nike UPCOMING창을 읽어옴
            .userAgent("19.0.1.84.52")
            .get()
        val elementsData = doc.select("li.launch-list-item")
        var channelId = 0

        for (elementData in elementsData) {
            val category = elementData.select("div.info-sect")
                .select("div.btn-box")
                .select("span.btn-link")
                .text()
            val specialUrl = "https://www.nike.com" + elementData.select("a").attr("href")

            if (category != "THE DRAW 진행예정" || mDao.existsSpecialData(specialUrl)) { // DRAW가 아니거나 이미 데이터가 존재할 시
                continue
            }

            val date = elementData.attr("data-active-date")
                .split(" ")[0]
            val year = date.split("-")[0]
            val month = elementData.select("div.img-sect")
                .select("div.date")
                .select("span.month")
                .text()
            val day = elementData.select("div.img-sect")
                .select("div.date")
                .select("span.day")
                .text()
            val whenStartEvent = elementData.select("div.info-sect")
                .select("div.text-box")
                .select("p.txt-subject")
                .text()
            val order = "$year${month.split("월")[0]}${day}".toInt()

            val specialShoesData = SpecialDataModel(
                null,
                specialUrl,
                year,
                month,
                day,
                whenStartEvent,
                order
            )

            insertSpecialShoesData(specialShoesData)

            val index = mDao.getAllSpecialShoesData()
                .indexOf(SpecialShoesDataModel(0, "", "", null, null, specialUrl))
            createNotification(mDao.getAllSpecialShoesData()[index], channelId)

            channelId++
        }
    }

    // 알림 생성
    private fun createNotification(data: SpecialShoesDataModel, channelId: Int) {
        // 자세히 보기
        val learnMoreIntent = Intent(mContext, MainActivity::class.java).apply {
            action = Contents.INTENT_ACTION_GOTO_WEBSITE
            putExtra(Contents.CHANNEL_ID, channelId)
            putExtra(Contents.DRAW_URL, data.ShoesUrl)
        }
        val setAlarmIntent = Intent(mContext, MainActivity::class.java).apply { // 알림 설정하기
            action = Contents.INTENT_ACTION_GOTO_DRAWLIST
        }
        val learnMorePendingIntent = PendingIntent.getActivity(
            mContext,
            channelId,
            learnMoreIntent,
            PendingIntent.FLAG_ONE_SHOT
        )
        val setAlarmPendingIntent =
            PendingIntent.getActivity(mContext, 100, setAlarmIntent, PendingIntent.FLAG_ONE_SHOT)

        val bitmap = Picasso.get().load(data.ShoesImageUrl).get()

        with(NotificationBuilder(applicationContext, Contents.CHANNEL_ID_FIND, "드로우 알림")) {
            imageNotification(
                "${data.ShoesSubTitle} - ${data.ShoesTitle}",
                data.ShoesPrice!!.split("\n")[0],
                bitmap,
                true
            )
            addActions(
                arrayOf("자세히 보기", "알림 설정하기"),
                arrayOf(learnMorePendingIntent, setAlarmPendingIntent)
            )

            buildNotify(channelId)
        }
    }

    // 데이터베이스 접근
    private fun insertSpecialShoesData(insertData: SpecialDataModel) {
        mDao.insertSpecialData(insertData)
    }

    private fun insertShoesData(insertData: ShoesDataModel) {
        mDao.insertShoesData(insertData)
    }

    private fun checkSpecialData() {
        for (shoesData in mDao.getAllSpecialShoesData()) {
            if (!allShoesList.contains(
                    ShoesDataModel(
                        0,
                        shoesData.ShoesSubTitle,
                        shoesData.ShoesTitle
                    )
                ) && shoesData.ShoesCategory == ShoesDataModel.CATEGORY_DRAW
            ) {
                deleteShoesData(shoesData)
            }
        }
    }

    private fun deleteShoesData(deleteData: SpecialShoesDataModel) {
        mDao.deleteShoesData(deleteData.ShoesTitle, deleteData.ShoesSubTitle)
        mDao.deleteSpecialData(deleteData.ShoesUrl!!)
    }
}