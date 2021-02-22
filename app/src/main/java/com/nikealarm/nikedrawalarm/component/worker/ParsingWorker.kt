package com.nikealarm.nikedrawalarm.component.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.nikealarm.nikedrawalarm.database.Dao
import com.nikealarm.nikedrawalarm.database.SpecialDataModel
import com.nikealarm.nikedrawalarm.database.ShoesDataModel
import com.nikealarm.nikedrawalarm.other.Contents
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import org.jsoup.Jsoup

@HiltWorker
class ParsingWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val mDao: Dao
) : Worker(
    context,
    workerParams
) {

    private val allShoesList = mutableListOf<ShoesDataModel>()

    override fun doWork(): Result {
        parsingData() // 데이터를 파싱함
        if (isStopped) { // cancel 됐을 때
            return Result.failure()
        }

        syncData() // 데이터를 갱신함

        Log.i("CheckSize", "${mDao.getAllShoesData().size}")
        Log.i("CheckDrawSize", "${mDao.getAllSpecialData().size}")
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
        val url = "https://www.nike.com/kr/launch/"
        val doc = Jsoup.connect(url) // nike SNKRS창을 읽어옴
            .userAgent("19.0.1.84.52")
            .get()

        val elementsData = doc.select("li.launch-list-item") // 여러개의 신발
        var progress = 0.0

        for (elementData in elementsData) {
            if (isStopped) { // cancel 됐을 때
                return
            }

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

            if (mDao.existsShoesData(shoesTitle, shoesSubTitle, innerUrl)) { // 해당 데이터가 이미 존재 시
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
                        shoesInfo,
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
                val shoesPrice = "가격 : " + innerDoc.select("div.price") // 신발 가격
                    .text()

                val shoesImageUrl = innerDoc.select("li.uk-width-1-2") // 신발 이미지
                    .select("img")
                    .eq(0)
                    .attr("src")

                val insertShoesData: ShoesDataModel
                when (shoesInfo) {
                    "THE DRAW 진행예정", "THE DRAW 응모하기" -> { // DRAW
                        val innerElementData = innerDoc.select("span.uk-text-bold")

                        var howToEvent = "" // 이벤트 참여방법
                        for (j in 0..2) {
                            howToEvent += innerElementData.select("p")
                                .eq(j)
                                .text() + "\n"
                        }
                        howToEvent += shoesPrice

                        insertShoesData = ShoesDataModel(
                            null,
                            shoesSubTitle,
                            shoesTitle,
                            howToEvent,
                            shoesImageUrl,
                            innerUrl,
                            ShoesDataModel.CATEGORY_DRAW
                        )
                    }
                    "THE DRAW 응모 마감", "THE DRAW 당첨 결과 확인", "THE DRAW 종료" -> { // DRAW END
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
                    "COMING SOON" -> { // COMING SOON
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
                    }
                    else -> { // RELEASED
                        val stock: String = if (shoesInfo == ShoesDataModel.SHOES_SOLD_OUT) {
                            shoesInfo
                        } else {
                            shoesPrice
                        }
                        insertShoesData = ShoesDataModel(
                            null,
                            shoesSubTitle,
                            shoesTitle,
                            stock,
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
            allShoesList.add(ShoesDataModel(0, shoesSubTitle, shoesTitle, null, null, innerUrl))
        }
    }

    // UPCOMING 파싱
    private fun parseSpecialData() {
        val url = "https://www.nike.com/kr/launch/?type=upcoming&activeDate=date-filter:AFTER"
        val doc = Jsoup.connect(url) // nike SNKRS창을 읽어옴
            .userAgent("19.0.1.84.52")
            .get()
        val elementsData = doc.select("li.launch-list-item")

        for (elementData in elementsData) {
            if (isStopped) { // cancel 됐을 때
                return
            }

            val category = elementData.select("div.info-sect")
                .select("div.btn-box")
                .select("span.btn-link")
                .text()
            val specialUrl = "https://www.nike.com" + elementData.select("a").attr("href")

            if (checkCategory(category) || mDao.existsSpecialData(specialUrl)) { // 이미 데이터 존재하지 않고 special이 아니면 continue
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

            val specialData =
                SpecialDataModel(null, specialUrl, year, month, day, whenStartEvent, order)
            insertSpecialData(specialData)
        }
    }

    private fun checkCategory(category: String): Boolean {
        return when (category) {
            "THE DRAW 진행예정", "COMING SOON" -> {
                false
            }
            else -> {
                true
            }
        }
    }

    // 갱신 설정
    // ShoesData 리스트를 갱신 함
    private fun checkShoesData() {

        if (allShoesList.size < mDao.getAllShoesData().size) {
            for (shoesData in mDao.getAllShoesData()) {

                if (!allShoesList.contains(shoesData)) {
                    mDao.deleteShoesData(shoesData.shoesTitle, shoesData.shoesSubTitle)
                }
            }
        }
    }

    // SpecialData 리스트를 갱신 함
    private fun checkSpecialData() {
        for (specialData in mDao.getAllSpecialData()) {

            if (!allShoesList.contains(
                    ShoesDataModel(
                        0,
                        "",
                        "",
                        null,
                        null,
                        specialData.specialUrl
                    )
                )
            ) {
                deleteSpecialData(specialData)
            }
        }
    }
    // 갱신 끝

    // 데이터베이스 설정
    private fun insertData(shoesData: ShoesDataModel) {
        mDao.insertShoesData(shoesData)
    }

    private fun updateData(newShoesData: ShoesDataModel) {
        val index = mDao.getAllShoesData()
            .indexOf(ShoesDataModel(0, "", "", null, null, newShoesData.shoesUrl))
        val ordinaryData = mDao.getAllShoesData()[index] // 기존의 있던 신발 데이터를 읽어옴

        if (newShoesData.shoesCategory != ordinaryData.shoesCategory) { // 카테고리가 바뀌었을 때
            if (ordinaryData.shoesCategory == ShoesDataModel.CATEGORY_COMING_SOON) { // COMING SOON -> RELEASED
                val newShoesPrice = try {
                    ordinaryData.shoesPrice?.split("\n")?.get(1) // 신발 가격
                } catch (e: ArrayIndexOutOfBoundsException) {
                    e.printStackTrace()
                    "가격 : "
                }

                mDao.updateShoesCategory(
                    newShoesPrice,
                    newShoesData.shoesCategory,
                    ordinaryData.shoesUrl!!
                )

                deleteSpecialData(SpecialDataModel(0, ordinaryData.shoesUrl))
            } else if (ordinaryData.shoesCategory == ShoesDataModel.CATEGORY_DRAW) { // DRAW -> DRAW END
                val newShoesPrice = "DRAW가 종료 되었습니다."

                mDao.updateShoesCategory(
                    newShoesPrice,
                    newShoesData.shoesCategory,
                    ordinaryData.shoesUrl!!
                )
                mDao.deleteSpecialData(newShoesData.shoesUrl!!)
            }
        }

        if (newShoesData.shoesCategory == ShoesDataModel.CATEGORY_RELEASED) { // 출시 된 상품의 재고가 바뀌었을 때
            if (ordinaryData.shoesPrice != ShoesDataModel.SHOES_SOLD_OUT && newShoesData.shoesPrice == ShoesDataModel.SHOES_SOLD_OUT) { // 재고가 다 떨어졌을 경우
                mDao.updateShoesCategory(
                    ShoesDataModel.SHOES_SOLD_OUT,
                    ShoesDataModel.CATEGORY_RELEASED,
                    ordinaryData.shoesUrl!!
                )
            } else if (ordinaryData.shoesPrice == ShoesDataModel.SHOES_SOLD_OUT && newShoesData.shoesPrice != ShoesDataModel.SHOES_SOLD_OUT) { // 재고가 다시 생긴 경우
                mDao.updateShoesCategory(
                    "가격 : ${newShoesData.shoesPrice}",
                    ShoesDataModel.CATEGORY_RELEASED,
                    ordinaryData.shoesUrl!!
                )
            }
        }

        if (newShoesData.shoesUrl != ordinaryData.shoesUrl) { // URL이 바뀌었을 시
            mDao.updateShoesUrl(
                newShoesData.shoesUrl,
                ordinaryData.shoesUrl
            )

            if (mDao.existsSpecialData(ordinaryData.shoesUrl!!)) { // Special이 존재 할 시
                mDao.updateSpecialDataUrl(newShoesData.shoesUrl!!, ordinaryData.shoesUrl)
            }
        }
    }

    private fun insertSpecialData(specialData: SpecialDataModel) {
        mDao.insertSpecialData(specialData)
    }

    private fun deleteSpecialData(delete: SpecialDataModel) {
        mDao.deleteSpecialData(delete.specialUrl)
    }
    // 데이터베이스 설정 끝
}