package com.nikealarm.nikedrawalarm

import com.nikealarm.nikedrawalarm.data.model.LaunchView
import com.nikealarm.nikedrawalarm.data.model.MerchProduct
import com.nikealarm.nikedrawalarm.data.retrofit.RetrofitService
import com.nikealarm.nikedrawalarm.domain.model.ProductCategory
import com.nikealarm.nikedrawalarm.domain.model.ProductState
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
//        runBlocking {
//            val builder = Retrofit.Builder()
//                .baseUrl(Contents.NIKE_API_URL)
//                .client(OkHttpClient.Builder().apply {
//                    readTimeout(2, TimeUnit.MINUTES)
//                }.build())
//                .addConverterFactory(GsonConverterFactory.create())
//                .build()
//            val retrofit = builder.create(RetrofitService::class.java)
//
//            val data = retrofit.getFeedProductData(anchor = 700, count = 100)
//
//            //product, multi_product
//            val productList = data.objects
//                .filter {
//                    it.publishedContent.properties.threadType == "product" ||
//                            it.publishedContent.properties.threadType == "multi_product"
//                }.map { filterProduct ->
//                    val productInfoList = filterProduct
//                        .publishedContent
//                        .nodes
//                        .filter { it.subType == "carousel" }
//                        .mapIndexed { index, nodes ->
//                            val explains: String =
//                                nodes.properties.jsonBody?.content?.get(0)?.content?.filter {
//                                    !it.text.contains("SNKRS")
//                                }?.get(0)?.text ?: ""
//
//                            try {
//                                ProductInfo(
//                                    productId = filterProduct.productInfo[index].merchProduct.id,
//                                    title = nodes.properties.title,
//                                    subTitle = nodes.properties.subtitle,
//                                    price = filterProduct.productInfo[index].merchPrice.currentPrice,
//                                    images = nodes.nodes!!.map { it.properties.squarishURL },
//                                    eventDate = getDateToLong(filterProduct.productInfo[index].launchView?.startEntryDate),
//                                    explains = explains,
//                                    sizes = filterProduct.productInfo[index].skus?.map {
//                                        it.countrySpecifications[0].localizedSize
//                                    } ?: listOf(),
//                                    url = Contents.NIKE_PRODUCT_URL + filterProduct.publishedContent.properties.seo.slug,
//                                    category = getShoesCategory(
//                                        filterProduct.productInfo[index].merchProduct,
//                                        filterProduct.productInfo[index].launchView
//                                    )
//                                )
//                            } catch (e: IndexOutOfBoundsException) {
//                                ProductInfo(
//                                    productId = "",
//                                    title = nodes.properties.title,
//                                    subTitle = nodes.properties.subtitle,
//                                    price = -1,
//                                    images = nodes.nodes!!.map { it.properties.squarishURL },
//                                    eventDate = 0L,
//                                    explains = explains,
//                                    sizes = listOf(),
//                                    url = Contents.NIKE_PRODUCT_URL + filterProduct.publishedContent.properties.seo.slug,
//                                    category = ProductCategory.Feed
//                                )
//                            }
//                        }
//                    val collection =
//                        if (filterProduct.publishedContent.nodes[0].subType == "image") {// 컬렉션 제품인 경우
//                            Collection(
//                                id = filterProduct.id,
//                                title = filterProduct.publishedContent.nodes[1].properties.title,
//                                subTitle = filterProduct.publishedContent.nodes[1].properties.subtitle,
//                                price = "컬렉션 제품",
//                                thumbnailImage = filterProduct.publishedContent.nodes[0].properties.portraitURL,
//                                explains = filterProduct.publishedContent.nodes[1].properties.jsonBody?.content?.get(
//                                    0
//                                )?.content?.get(0)?.text ?: "",
//                                url = Contents.NIKE_PRODUCT_URL + filterProduct.publishedContent.properties.seo.slug
//                            )
//                        } else {
//                            null
//                        }
//
//                    Product(
//                        collection = collection,
//                        productInfoList = productInfoList
//                    )
//                    data
//                }
//
//            println("listSize: ${productList.size}")
//        }
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