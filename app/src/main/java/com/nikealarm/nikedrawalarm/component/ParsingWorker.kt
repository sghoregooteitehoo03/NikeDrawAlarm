package com.nikealarm.nikedrawalarm.component

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.nikealarm.nikedrawalarm.database.Dao
import com.nikealarm.nikedrawalarm.database.DrawShoesDataModel
import com.nikealarm.nikedrawalarm.database.MyDataBase
import com.nikealarm.nikedrawalarm.database.ShoesDataModel
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jsoup.Jsoup

class ParsingWorker(context: Context, workerParams: WorkerParameters) : Worker(
    context,
    workerParams
) {
    private lateinit var mDao: Dao

    override fun doWork(): Result {
        mDao = MyDataBase.getDatabase(applicationContext)!!.getDao()

        clearData()
        parsingData()
        return Result.success()
    }

    private fun parsingData() {
        val url = "https://www.nike.com/kr/launch/?type=feed"
        val doc = Jsoup.connect(url) // nike SNKRS창을 읽어옴
            .userAgent("Mozilla")
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
                "https://www.nike.com" + elementData.select("a").attr("href") // 해당 draw 링크창을 읽어옴
            val innerDoc = Jsoup.connect(innerUrl)
                .userAgent("Mozilla")
                .get()

            // 신발 정보를 가져옴
            val shoesPrice = innerDoc.select("div.price") // draw 신발 가격
                .text()
            val shoesImageUrl = innerDoc.select("li.uk-width-1-2") // draw 신발 이미지
                .select("img")
                .eq(0)
                .attr("src")

            val insertShoesData: ShoesDataModel
            when (shoesInfo) {
                "THE DRAW 진행예정" -> {
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
                    //                howToEvent += "\n$shoesPrice"
                }
                "COMING SOON" -> {
                    insertShoesData = ShoesDataModel(
                        null,
                        shoesSubTitle,
                        shoesTitle,
                        shoesPrice,
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
    }

    // 데이터베이스 설정
    private fun insertData(shoesData: ShoesDataModel) {
        CoroutineScope(Dispatchers.IO).launch {
            mDao.insertShoesData(shoesData)
        }
    }

    private fun clearData() {
        CoroutineScope(Dispatchers.IO).launch {
            mDao.clearShoesData()
        }
    }
}