package com.nikealarm.nikedrawalarm.component

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.nikealarm.nikedrawalarm.database.Dao
import com.nikealarm.nikedrawalarm.database.DrawShoesDataModel
import com.nikealarm.nikedrawalarm.database.MyDataBase
import com.nikealarm.nikedrawalarm.database.ShoesDataModel
import com.squareup.picasso.Picasso
import org.jsoup.Jsoup

class ParsingWorker(context: Context, workerParams: WorkerParameters) : Worker(
    context,
    workerParams
) {
    private lateinit var mDao: Dao

    private val allShoesList = mutableListOf<ShoesDataModel>()
    private val notDrawShoesList = mutableListOf<DrawShoesDataModel>()

    override fun doWork(): Result {
        mDao = MyDataBase.getDatabase(applicationContext)!!.getDao()

        var size = mDao.getAllShoesData().size
        Log.i("CheckSize", "$size")
        Log.i("CheckDrawSize", "${mDao.getAllDrawShoesData().size}")

        parsingData()

        size = mDao.getAllShoesData().size
        Log.i("CheckSize", "$size")
        return Result.success()
    }

    private fun parsingData() {
        val url = "https://www.nike.com/kr/launch/?type=feed"
        val doc = Jsoup.connect(url) // nike SNKRS창을 읽어옴
            .userAgent("19.0.1.84.52")
            .get()

        val elementsData = doc.select("div.launch-list-item") // 여러개의 신발

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
            val innerUrl =
                "https://www.nike.com" + elementData.select("a").attr("href") // 해당 신발의 링크창을 읽어옴

            if (mDao.getAllShoesData().contains(ShoesDataModel(0, shoesSubTitle, shoesTitle))) {
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
            } else {
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

                        if (!mDao.getAllDrawShoesData()
                                .contains(DrawShoesDataModel(0, shoesSubTitle, shoesTitle))
                        ) {
                            insertDrawData(
                                DrawShoesDataModel(
                                    null,
                                    shoesSubTitle,
                                    shoesTitle,
                                    howToEvent,
                                    Picasso.get().load(shoesImageUrl).get(),
                                    innerUrl
                                )
                            )
                        }
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

            allShoesList.add(ShoesDataModel(0, shoesSubTitle, shoesTitle))
            notDrawShoesList.add(DrawShoesDataModel(0, shoesSubTitle, shoesTitle))
        }

        checkShoesData()
        checkDrawData()
    }

    // 데이터베이스 설정
    private fun insertData(shoesData: ShoesDataModel) {
        mDao.insertShoesData(shoesData)
    }

    private fun updateData(newShoesData: ShoesDataModel) {
        val index = mDao.getAllShoesData()
            .indexOf(ShoesDataModel(0, newShoesData.shoesSubTitle, newShoesData.shoesTitle))
        val ordinaryData = mDao.getAllShoesData()[index]

        if (newShoesData.shoesCategory != ordinaryData.shoesCategory) {
            if (ordinaryData.shoesCategory == ShoesDataModel.CATEGORY_COMING_SOON) {
                val newShoesPrice = ordinaryData.shoesPrice?.split("\n")?.get(1) // 신발 가격

                mDao.updateShoesCategory(
                    newShoesPrice,
                    newShoesData.shoesCategory,
                    newShoesData.shoesTitle,
                    newShoesData.shoesSubTitle
                )
            } else if (ordinaryData.shoesCategory == ShoesDataModel.CATEGORY_DRAW) {
                val newShoesPrice = "DRAW가 종료 되었습니다."

                mDao.updateShoesCategory(
                    newShoesPrice,
                    newShoesData.shoesCategory,
                    newShoesData.shoesTitle,
                    newShoesData.shoesSubTitle
                )
            }
        }

        if (newShoesData.shoesUrl != ordinaryData.shoesUrl) {
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

    private fun checkShoesData() {

        if (allShoesList.size < mDao.getAllShoesData().size) {
            for (shoesData in mDao.getAllShoesData()) {

                if (!allShoesList.contains(shoesData)) {
                    mDao.deleteShoesData(shoesData)
                }
            }
        }
    }

    private fun insertDrawData(drawShoesData: DrawShoesDataModel) {
        mDao.insertDrawShoesData(drawShoesData)
    }

    private fun checkDrawData() {
        for (shoesData in mDao.getAllDrawShoesData()) {

            if (!notDrawShoesList.contains(shoesData)) {
                deleteDrawData(shoesData)
            }
        }
    }

    private fun deleteDrawData(deleteShoes: DrawShoesDataModel) {
        mDao.deleteDrawShoesData(deleteShoes.shoesTitle, deleteShoes.shoesSubTitle)
    }

}