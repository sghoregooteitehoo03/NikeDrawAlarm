package com.nikealarm.nikedrawalarm

import com.nikealarm.nikedrawalarm.data.model.LaunchView
import com.nikealarm.nikedrawalarm.data.model.MerchProduct
import com.nikealarm.nikedrawalarm.data.retrofit.RetrofitService
import com.nikealarm.nikedrawalarm.domain.model.ProductCategory
import com.nikealarm.nikedrawalarm.domain.model.ProductState
import com.nikealarm.nikedrawalarm.domain.model.getProductFilter
import com.nikealarm.nikedrawalarm.domain.model.translateToProductInfoList
import com.nikealarm.nikedrawalarm.util.Constants
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import org.junit.Test
import org.junit.Assert.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Locale
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
    fun getProductTest() {
        runBlocking {
            val builder = Retrofit.Builder()
                .baseUrl(Constants.NIKE_API_URL)
                .client(OkHttpClient.Builder().apply {
                    readTimeout(2, TimeUnit.MINUTES)
                }.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val retrofit = builder.create(RetrofitService::class.java)

//            air-force-1-low-chocolate
            val data = retrofit.getProductInfo("seoSlugs%28air-force-1-low-chocolate%29")
            print(data)
        }
    }

    @Test
    fun parsingTest() {
        runBlocking {
            val builder = Retrofit.Builder()
                .baseUrl(Constants.NIKE_API_URL)
                .client(OkHttpClient.Builder().apply {
                    readTimeout(2, TimeUnit.MINUTES)
                }.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val retrofit = builder.create(RetrofitService::class.java)

            val data = retrofit.getFeedProducts(anchor = 700, count = 100)

            //product, multi_product
            val productList = data.objects
                .filter {
                    getProductFilter(it)
                }.map { filterProduct ->
                    translateToProductInfoList(filterProduct)
                }

            println("listSize: ${productList.size}")
        }
    }

    @Test
    fun functionTest() {
        val test = "https://www.nike.com/kr/launch/t/air-force-1-low-chocolate"
        println(test.substringAfter("t/"))
    }

    private fun getDateToLong(date: String?): Long {
        if (date == null) {
            return 0L
        }

        val dateFormat = SimpleDateFormat(
            "yyyy-MM-dd'T'hh:mm:ss.SSS'Z'",
            Locale.KOREA
        )

        return (dateFormat.parse(date)?.time?.plus(32400000) ?: 0L)  // (+9 Hours) UTC -> KOREA
    }

    private fun getShoesCategory(
        merchProduct: MerchProduct,
        launchView: LaunchView?
    ): ProductCategory {
        return if (launchView != null) {
            if (launchView.stopEntryDate != null && merchProduct.commerceEndDate == null) {
                ProductCategory.Draw
            } else {
                if (merchProduct.status == ProductState.PRODUCT_STATUS_INACTIVE && merchProduct.commerceEndDate != null) {
                    ProductCategory.SoldOut
                } else {
                    ProductCategory.Coming
                }
            }
        } else {
            ProductCategory.Feed
        }
    }
}