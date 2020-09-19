package com.nikealarm.nikedrawalarm.component

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.nikealarm.nikedrawalarm.R
import com.nikealarm.nikedrawalarm.database.SpecialShoesDataModel
import com.nikealarm.nikedrawalarm.database.MyDataBase
import com.nikealarm.nikedrawalarm.database.ShoesDataModel
import com.nikealarm.nikedrawalarm.other.Contents
import com.nikealarm.nikedrawalarm.ui.MainActivity
import com.squareup.picasso.Picasso
import org.jsoup.Jsoup

class FindDrawWorker(context: Context, workerParams: WorkerParameters) : Worker(
    context,
    workerParams
) {
    private val mContext = context
    private val mDao = MyDataBase.getDatabase(mContext)!!.getDao()

    private val allShoesList = mutableListOf<ShoesDataModel>()
    private val specialShoesList = mutableListOf<SpecialShoesDataModel>()

    override fun doWork(): Result {
        parseData()

        return Result.success()
    }

    // 크롤링
    private fun parseData() {
        parseReleasedData()
        parseSpecialData()

        checkDrawData()
    }

    private fun parseReleasedData() {
        val url = "https://www.nike.com/kr/launch/?type=feed"
        val doc = Jsoup.connect(url) // nike UPCOMING 창을 읽어옴
            .userAgent("19.0.1.84.52")
            .get()
        val elementsData = doc.select("div.launch-list-item")

        var channelId = 0
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

            // draw가 없을 시
            if (!mDao.getAllSpecialShoesData().contains(
                    SpecialShoesDataModel(
                        0,
                        shoesSubTitle,
                        shoesTitle
                    )
                )
            ) {
                if (shoesInfo == "THE DRAW 진행예정") {
                    val innerUrl = "https://www.nike.com" + elementData.select("a")
                        .attr("href") // 해당 draw 링크창을 읽어옴
                    val innerDoc = Jsoup.connect(innerUrl)
                        .userAgent("19.0.1.84.52")
                        .get()

                    // 신발 정보를 가져옴
                    val shoesPrice = innerDoc.select("div.price") // draw 신발 가격
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

                    howToEvent += "\n$shoesPrice"

                    val drawData = SpecialShoesDataModel(
                        null,
                        shoesSubTitle,
                        shoesTitle,
                        howToEvent,
                        innerUrl,
                        shoesImageUrl
                    )

                    specialShoesList.add(drawData)
                }
            }

            channelId++
            allShoesList.add(ShoesDataModel(0, shoesSubTitle, shoesTitle))
        }
    }

    private fun parseSpecialData() {
        val url = "https://www.nike.com/kr/launch/?type=upcoming&activeDate=date-filter:AFTER"
        val doc = Jsoup.connect(url) // nike UPCOMING창을 읽어옴
            .userAgent("19.0.1.84.52")
            .get()
        val elementsData = doc.select("div.launch-list-item")

        for (elementData in elementsData) {
            val category = elementData.select("div.info-sect")
                .select("div.btn-box")
                .select("span.btn-link")
                .text()

            if (category != "THE DRAW 진행예정") { // DRAW만 읽어옴
                continue
            }

            val shoesSubTitle = elementData.select("div.info-sect")
                .select("div.text-box")
                .select("p.txt-description")
                .text()

            var position = 0
            for (data in specialShoesList) {
                if (data.shoesSubTitle == shoesSubTitle) {
                    val whenStartEvent = elementData.select("div.info-sect")
                        .select("div.text-box")
                        .select("p.txt-subject")
                        .text()
                    val month = elementData.select("div.img-sect")
                        .select("div.date")
                        .select("span.month")
                        .text()
                    val day = elementData.select("div.img-sect")
                        .select("div.date")
                        .select("span.day")
                        .text()
                    val order = "${month.split("월")[0]}${day}".toInt()

                    val specialShoesData = SpecialShoesDataModel(
                        null,
                        data.shoesSubTitle,
                        data.shoesTitle,
                        data.howToEvent,
                        data.shoesUrl,
                        data.shoesImage,
                        month,
                        day,
                        whenStartEvent,
                        order
                    )

                    if (!mDao.getAllSpecialShoesData().contains(specialShoesData)){
                        insertSpecialShoesData(specialShoesData)
                        createNotification(specialShoesData, position)
                    }
                }

                position++
            }
        }
    }

    // 알림 생성
    private fun createNotification(shoesData: SpecialShoesDataModel, channelId: Int) {
        val vibrate = LongArray(4).apply {
            set(0, 0)
            set(1, 100)
            set(2, 200)
            set(3, 300)
        }

        // 자세히 보기
        val learnMoreIntent = Intent(mContext, MainActivity::class.java).apply {
            action = Contents.INTENT_ACTION_GOTO_WEBSITE
            putExtra(Contents.CHANNEL_ID, channelId)
            putExtra(Contents.DRAW_URL, shoesData.shoesUrl)
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

        val bitmap = Picasso.get().load(shoesData.shoesImage).get()
        val notificationBuilder = NotificationCompat.Builder(mContext, "Default")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("${shoesData.shoesSubTitle} - ${shoesData.shoesTitle}")
            .setVibrate(vibrate)
            .setLargeIcon(bitmap)
            .setStyle(NotificationCompat.BigTextStyle())
            .setStyle(
                NotificationCompat.BigPictureStyle()
                    .bigPicture(bitmap)
                    .bigLargeIcon(null)
            )
            .setContentText(shoesData.howToEvent!!.split("\n")[0])
            .setAutoCancel(true)
            .addAction(0, "자세히 보기", learnMorePendingIntent)
            .addAction(0, "알림 설정하기", setAlarmPendingIntent)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "Default",
                shoesData.shoesTitle,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val notificationManager =
                mContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            notificationManager.createNotificationChannel(channel)
        }

        with(NotificationManagerCompat.from(mContext)) {
            notify(channelId, notificationBuilder.build())
        }
    }

    // 데이터베이스 접근
    private fun insertSpecialShoesData(insertShoesData: SpecialShoesDataModel) {
        mDao.insertSpecialShoesData(insertShoesData)
    }

    private fun checkDrawData() {
        for (shoesData in mDao.getAllSpecialShoesData()) {
            if (!allShoesList.contains(ShoesDataModel(0, shoesData.shoesSubTitle, shoesData.shoesTitle))) {
                deleteShoesData(shoesData)
            }
        }
    }

    private fun deleteShoesData(deleteData: SpecialShoesDataModel) {
        mDao.deleteSpecialShoesData(deleteData.shoesTitle, deleteData.shoesSubTitle)
    }
}