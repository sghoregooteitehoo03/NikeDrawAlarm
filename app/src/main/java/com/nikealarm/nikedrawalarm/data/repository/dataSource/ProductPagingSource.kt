package com.nikealarm.nikedrawalarm.data.repository.dataSource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.nikealarm.nikedrawalarm.data.model.LaunchView
import com.nikealarm.nikedrawalarm.data.model.MerchProduct
import com.nikealarm.nikedrawalarm.data.retrofit.RetrofitService
import com.nikealarm.nikedrawalarm.domain.model.Collection
import com.nikealarm.nikedrawalarm.domain.model.Product
import com.nikealarm.nikedrawalarm.domain.model.ProductCategory
import com.nikealarm.nikedrawalarm.domain.model.ProductInfo
import com.nikealarm.nikedrawalarm.domain.model.ProductState
import com.nikealarm.nikedrawalarm.domain.model.getDateToLong
import com.nikealarm.nikedrawalarm.domain.model.getShoesCategory
import com.nikealarm.nikedrawalarm.util.Constants
import java.text.SimpleDateFormat
import java.util.Locale

// TODO: 출시 기간이 지난 상품은 eventDate 0으로 초기화 시키기

class ProductPagingSource(
    private val retrofitService: RetrofitService,
    private val isUpcoming: Boolean
) : PagingSource<Int, Product>() {
    override fun getRefreshKey(state: PagingState<Int, Product>): Int? {
        return state.anchorPosition?.let {
            state.closestPageToPosition(it)?.prevKey
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Product> {
        return try {
            val key = params.key ?: 0
            val data = if (isUpcoming) {
                retrofitService.getUpcomingProducts(key)
            } else {
                retrofitService.getFeedProducts(key)
            }

            if (data.objects.isEmpty())
                throw NullPointerException()

            val productList = data.objects
                .filter { // 제품들에 관해서만 필터링, Test 제품 걸러내기
                    (it.publishedContent.properties.threadType == "product" || it.publishedContent.properties.threadType == "multi_product")
                            && it.publishedContent.nodes.size > 1
                }.map { filterProduct ->
                    val productInfoList = filterProduct
                        .publishedContent
                        .nodes
                        .filter { it.subType == "carousel" } // 제품들만 필터링
                        .mapIndexed { index, nodes ->
                            val explains: String =
                                nodes.properties.jsonBody?.content?.get(0)?.content?.filter {
                                    !it.text.contains("SNKRS")
                                }?.get(0)?.text ?: ""

                            try {
                                ProductInfo(
                                    productId = filterProduct.productInfo[index].merchProduct.id,
                                    title = nodes.properties.title,
                                    subTitle = nodes.properties.subtitle,
                                    price = filterProduct.productInfo[index].merchPrice.currentPrice,
                                    images = nodes.nodes!!.map { it.properties.squarishURL },
                                    eventDate = getDateToLong(filterProduct.productInfo[index].launchView?.startEntryDate),
                                    explains = explains,
                                    sizes = filterProduct.productInfo[index].skus?.map {
                                        it.countrySpecifications[0].localizedSize
                                    } ?: listOf(),
                                    url = Constants.NIKE_PRODUCT_URL + filterProduct.publishedContent.properties.seo.slug,
                                    category = getShoesCategory(
                                        filterProduct.productInfo[index].merchProduct,
                                        filterProduct.productInfo[index].launchView
                                    )
                                )
                            } catch (e: IndexOutOfBoundsException) {
                                ProductInfo(
                                    productId = "",
                                    title = nodes.properties.title,
                                    subTitle = nodes.properties.subtitle,
                                    price = -1,
                                    images = nodes.nodes!!.map { it.properties.squarishURL },
                                    eventDate = 0L,
                                    explains = explains,
                                    sizes = listOf(),
                                    url = Constants.NIKE_PRODUCT_URL + filterProduct.publishedContent.properties.seo.slug,
                                    category = ProductCategory.Feed
                                )
                            }
                        }
                    val collection =
                        if (filterProduct.publishedContent.nodes[0].subType == "image") { // 컬렉션 제품인 경우
                            Collection(
                                id = filterProduct.id,
                                title = filterProduct.publishedContent.nodes[1].properties.title,
                                subTitle = filterProduct.publishedContent.nodes[1].properties.subtitle,
                                price = "컬렉션 제품",
                                thumbnailImage = filterProduct.publishedContent.nodes[0].properties.portraitURL,
                                explains = filterProduct.publishedContent.nodes[1].properties.jsonBody?.content?.get(
                                    0
                                )?.content?.get(0)?.text ?: "",
                                url = Constants.NIKE_PRODUCT_URL + filterProduct.publishedContent.properties.seo.slug
                            )
                        } else {
                            null
                        }

                    Product(
                        collection = collection,
                        productInfoList = productInfoList.filter { it.price != -1 } // 오류인 상품은 제외 시킴
                    )
                }

            LoadResult.Page(
                data = productList,
                prevKey = null,
                nextKey = key + 50
            )
        } catch (e: Exception) {
            e.printStackTrace()
            LoadResult.Error(e)
        }
    }
}