package com.nikealarm.nikedrawalarm.data.repository.dataSource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.nikealarm.nikedrawalarm.data.retrofit.RetrofitService
import com.nikealarm.nikedrawalarm.domain.model.Collection
import com.nikealarm.nikedrawalarm.domain.model.Product
import com.nikealarm.nikedrawalarm.domain.model.getProductFilter
import com.nikealarm.nikedrawalarm.domain.model.translateToProductInfoList
import com.nikealarm.nikedrawalarm.util.Constants

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
            val key: Int = params.key ?: 0
            val data = if (isUpcoming) {
                retrofitService.getUpcomingProducts(key)
            } else {
                retrofitService.getFeedProducts(key)
            }

            val productList = data.objects
                .filter { // 제품들에 관해서만 필터링, Test 제품 걸러내기
                    getProductFilter(it)
                }.map { filterProduct ->
                    val productInfoList = translateToProductInfoList(filterProduct)
                    val collection =
                        if (filterProduct.publishedContent.nodes[0].subType == "image") { // 컬렉션 제품인 경우
                            var explains = ""
                            val contents = filterProduct.publishedContent.nodes[1]
                                .properties
                                .jsonBody
                                ?.content
                                ?.get(0)?.content ?: listOf()

                            for (i in contents.indices) {
                                if (contents[i].text.contains("제품 출시 전")) {
                                    break
                                }

                                explains += "${contents[i].text}\n\n"
                            }
                            // 기존에 있는 띄어쓰기를 지움
                            explains = explains.trimEnd('\n').trimEnd('\n')

                            Collection(
                                id = filterProduct.id,
                                title = filterProduct.publishedContent.nodes[1].properties.title,
                                subTitle = filterProduct.publishedContent.nodes[1].properties.subtitle,
                                price = "컬렉션 제품",
                                thumbnailImage = filterProduct.publishedContent.nodes[0].properties.portraitURL,
                                explains = explains,
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
                nextKey = if (data.objects.isNotEmpty()) {
                    key + 50
                } else {
                    null
                }
            )
        } catch (e: Exception) {
            e.printStackTrace()
            LoadResult.Error(e)
        }
    }
}