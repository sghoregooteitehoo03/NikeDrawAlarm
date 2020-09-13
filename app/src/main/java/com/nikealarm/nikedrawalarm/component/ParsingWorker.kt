package com.nikealarm.nikedrawalarm.component

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.nikealarm.nikedrawalarm.database.Dao
import com.nikealarm.nikedrawalarm.database.SpecialShoesDataModel
import com.nikealarm.nikedrawalarm.database.MyDataBase
import com.nikealarm.nikedrawalarm.database.ShoesDataModel
import com.nikealarm.nikedrawalarm.other.Contents
import org.jsoup.Jsoup

class ParsingWorker(context: Context, workerParams: WorkerParameters) : Worker(
    context,
    workerParams
) {
    private lateinit var mDao: Dao

    private val allShoesList = mutableListOf<ShoesDataModel>()
    private val specialShoesList = mutableListOf<SpecialShoesDataModel>()

    override fun doWork(): Result {
        mDao = MyDataBase.getDatabase(applicationContext)!!.getDao()

        parsingData()
        syncData()

        val size = mDao.getAllShoesData().size
        Log.i("CheckSize", "$size")
        Log.i("CheckDrawSize", "${mDao.getAllSpecialShoesData().size}")
        return Result.success()
    }

    // 데이터 파싱
    private fun parsingData() {
        parseReleasedData()
        parseSpecialData()
    }

    // 데이터 갱신
    private fun syncData() {
        checkShoesData()
        checkSpecialData()
    }

    // FEED 파싱
    private fun parseReleasedData() {
        val url = "https://www.nike.com/kr/launch/?type=feed"
        val doc = Jsoup.connect(url) // nike SNKRS창을 읽어옴
            .userAgent("19.0.1.84.52")
            .get()

        val elementsData = doc.select("div.launch-list-item") // 여러개의 신발
        var progress = 0.0

        for (elementData in elementsData) {
            val shoesInfo = elementData.select("div.info-sect") // 신발 정보
                .select("div.btn-box")
                .select("span")
                .text()

            if (shoesInfo == "LEARN MORE") {
                progress += 2.5
                setProgressAsync(workDataOf(Contents.WORKER_PARSING_DATA_OUTPUT_KEY to progress.toInt()))

                continue
            }

            val shoesSubTitle = elementData.select("div.text-box")
                .select("p.txt-subject")
                .text()
            val shoesTitle = elementData.select("div.text-box")
                .select("p.txt-description")
                .text()
            val innerUrl =
                "https://www.nike.com" + elementData.select("a").attr("href") // 해당 신발의 링크창을 읽어옴

            if (mDao.getAllShoesData().contains(ShoesDataModel(0, shoesSubTitle, shoesTitle))) { // 해당 데이터가 이미 존재 시
                val category = when (shoesInfo) {
                    "THE DRAW 진행예정", "THE DRAW 응모하기" -> ShoesDataModel.CATEGORY_DRAW
                    "THE DRAW 응모 마감", "THE DRAW 당첨 결과 확인", "THE DRAW 종료" -> ShoesDataModel.CATEGORY_DRAW_END
                    "COMING SOON" -> ShoesDataModel.CATEGORY_COMING_SOON
                    else -> ShoesDataModel.CATEGORY_RELEASED
                }

                updateData(
                    ShoesDataModel(
                        0,
                        shoesSubTitle,
                        shoesTitle,
                        null,
                        null,
                        innerUrl,
                        category
                    )
                )
            } else { // 존재하지 않을 시
                val innerDoc = Jsoup.connect(innerUrl)
                    .userAgent("19.0.1.84.52")
                    .get()

                // 신발 정보를 가져옴
                val shoesPrice = innerDoc.select("div.price") // 신발 가격
                    .text()

                val shoesImageUrl = innerDoc.select("li.uk-width-1-2") // 신발 이미지
                    .select("img")
                    .eq(0)
                    .attr("src")

                val insertShoesData: ShoesDataModel
                when (shoesInfo) {
                    "THE DRAW 진행예정", "THE DRAW 응모하기" -> {
                        val innerElementData = innerDoc.select("span.uk-text-bold")

                        var howToEvent = "" // 이벤트 참여방법
                        for (j in 0..2) {
                            howToEvent += innerElementData.select("p")
                                .eq(j)
                                .text() + "\n"
                        }

                        insertShoesData = ShoesDataModel(
                            null,
                            shoesSubTitle,
                            shoesTitle,
                            howToEvent,
                            shoesImageUrl,
                            innerUrl,
                            ShoesDataModel.CATEGORY_DRAW
                        )

                        specialShoesList.add(
                            SpecialShoesDataModel(
                                0,
                                shoesSubTitle,
                                shoesTitle,
                                howToEvent,
                                innerUrl,
                                shoesImageUrl
                            )
                        )
                    }
                    "THE DRAW 응모 마감", "THE DRAW 당첨 결과 확인", "THE DRAW 종료" -> {
                        insertShoesData = ShoesDataModel(
                            null,
                            shoesSubTitle,
                            shoesTitle,
                            "DRAW가 종료 되었습니다.",
                            shoesImageUrl,
                            innerUrl,
                            ShoesDataModel.CATEGORY_DRAW_END
                        )
                    }
                    "COMING SOON" -> {
                        val launchDate = "${innerDoc.select("div.txt-date").text()}\n${shoesPrice}"

                        insertShoesData = ShoesDataModel(
                            null,
                            shoesSubTitle,
                            shoesTitle,
                            launchDate,
                            shoesImageUrl,
                            innerUrl,
                            ShoesDataModel.CATEGORY_COMING_SOON
                        )

                        specialShoesList.add(
                            SpecialShoesDataModel(
                                0,
                                shoesSubTitle,
                                shoesTitle,
                                launchDate,
                                innerUrl,
                                shoesImageUrl
                            )
                        )
                    }
                    else -> {
                        insertShoesData = ShoesDataModel(
                            null,
                            shoesSubTitle,
                            shoesTitle,
                            shoesPrice,
                            shoesImageUrl,
                            innerUrl,
                            ShoesDataModel.CATEGORY_RELEASED
                        )
                    }
                }

                insertData(insertShoesData)
            }

            progress += 2.5

            setProgressAsync(workDataOf(Contents.WORKER_PARSING_DATA_OUTPUT_KEY to progress.toInt()))
            allShoesList.add(ShoesDataModel(0, shoesSubTitle, shoesTitle))
        }
    }

    // UPCOMING 파싱
    private fun parseSpecialData() {
        val url = "https://www.nike.com/kr/launch/?type=upcoming&activeDate=date-filter:AFTER"
        val doc = Jsoup.connect(url) // nike SNKRS창을 읽어옴
            .userAgent("19.0.1.84.52")
            .get()
        val elementsData = doc.select("div.launch-list-item")

        for (elementData in elementsData) {
            val category = elementData.select("div.info-sect")
                .select("div.btn-box")
                .select("span.btn-link")
                .text()

            if (checkCategory(category)) { // DRAW와 COMINGSOON 만 읽어옴
                continue
            }

            val shoesSubTitle = elementData.select("div.info-sect")
                .select("div.text-box")
                .select("p.txt-description")
                .text()

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

                    val specialShoesData = SpecialShoesDataModel(
                        null,
                        data.shoesSubTitle,
                        data.shoesTitle,
                        data.howToEvent,
                        data.shoesUrl,
                        data.shoesImage,
                        month,
                        day,
                        whenStartEvent
                    )

                    if(!mDao.getAllSpecialShoesData().contains(specialShoesData)) {
                        insertSpecialData(specialShoesData)
                    }
                }
            }
        }
    }

    private fun checkCategory(category: String): Boolean {
        return when (category) {
            "THE DRAW 진행예정", "THE DRAW 응모하기", "COMING SOON" -> {
                false
            }
            else -> {
                true
            }
        }
    }

    // 데이터베이스 설정
    private fun insertData(shoesData: ShoesDataModel) {
        mDao.insertShoesData(shoesData)
    }

    private fun updateData(newShoesData: ShoesDataModel) {
        val index = mDao.getAllShoesData()
            .indexOf(ShoesDataModel(0, newShoesData.shoesSubTitle, newShoesData.shoesTitle))
        val ordinaryData = mDao.getAllShoesData()[index]

        if (newShoesData.shoesCategory != ordinaryData.shoesCategory) { // CATEGORY -> RELEASED
            if (ordinaryData.shoesCategory == ShoesDataModel.CATEGORY_COMING_SOON) {
                val newShoesPrice = ordinaryData.shoesPrice?.split("\n")?.get(1) // 신발 가격

                mDao.updateShoesCategory(
                    newShoesPrice,
                    newShoesData.shoesCategory,
                    newShoesData.shoesTitle,
                    newShoesData.shoesSubTitle
                )

                deleteSpecialData(SpecialShoesDataModel(0, newShoesData.shoesSubTitle, newShoesData.shoesTitle))
            } else if (ordinaryData.shoesCategory == ShoesDataModel.CATEGORY_DRAW) { // DRAW -> DRAW END
                val newShoesPrice = "DRAW가 종료 되었습니다."

                mDao.updateShoesCategory(
                    newShoesPrice,
                    newShoesData.shoesCategory,
                    newShoesData.shoesTitle,
                    newShoesData.shoesSubTitle
                )
            }
        }

        if (newShoesData.shoesUrl != ordinaryData.shoesUrl) { // URL이 바뀌었을 시
            mDao.updateShoesUrl(
                newShoesData.shoesUrl,
                newShoesData.shoesTitle,
                newShoesData.shoesSubTitle
            )
        }
    }

    private fun clearData() {
        mDao.clearShoesData()
    }

    // ShoesData 리스트를 갱신 함
    private fun checkShoesData() {

        if (allShoesList.size < mDao.getAllShoesData().size) {
            for (shoesData in mDao.getAllShoesData()) {

                if (!allShoesList.contains(shoesData)) {
                    mDao.deleteShoesData(shoesData)
                }
            }
        }
    }

    private fun insertSpecialData(specialShoesData: SpecialShoesDataModel) {
        mDao.insertSpecialShoesData(specialShoesData)
    }

    // SpecialData 리스트를 갱신 함
    private fun checkSpecialData() {
        for (shoesData in mDao.getAllSpecialShoesData()) {

            if (!allShoesList.contains(ShoesDataModel(0, shoesData.shoesSubTitle, shoesData.shoesTitle))) {
                deleteSpecialData(shoesData)
            }
        }
    }

    private fun deleteSpecialData(deleteShoes: SpecialShoesDataModel) {
        mDao.deleteSpecialShoesData(deleteShoes.shoesTitle, deleteShoes.shoesSubTitle)
    }
}