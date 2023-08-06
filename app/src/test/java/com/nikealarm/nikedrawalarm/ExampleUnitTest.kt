package com.nikealarm.nikedrawalarm

import com.nikealarm.nikedrawalarm.database.ShoesDataModel
import com.nikealarm.nikedrawalarm.other.RetrofitService
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import org.junit.Test

import org.junit.Assert.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun test() {
        runBlocking {
            val url = "https://api.nike.com/"
            val builder = Retrofit.Builder()
                .baseUrl(url)
                .client(OkHttpClient.Builder().apply {
                    readTimeout(2, TimeUnit.MINUTES)
                }.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val retrofit = builder.create(RetrofitService::class.java)

            val data = retrofit.getSnkrsData(anchor = 0)
            var index = 0
            val shoesList = data.objects
                .filter {
                    it.publishedContent.properties.threadType == "product"
                }
                .map { shoes ->
                    val shoesUrl = "https://www.nike.com/kr/launch/t/"

                    index++
                    ShoesDataModel(
                        id = null,
                        shoesSubTitle = shoes.publishedContent.properties.coverCard.properties.subtitle,
                        shoesTitle = shoes.publishedContent.properties.coverCard.properties.title,
                        shoesPrice = shoes.productInfo[0].merchPrice.currentPrice.toString(),
                        shoesImageUrl = shoes.publishedContent.properties.coverCard.properties.squarishURL,
                        shoesUrl = shoesUrl + shoes.publishedContent.properties.seo.slug,
                        shoesCategory = ShoesDataModel.CATEGORY_RELEASED
                    )
                }

            println("list: $shoesList")
        }
    }
}