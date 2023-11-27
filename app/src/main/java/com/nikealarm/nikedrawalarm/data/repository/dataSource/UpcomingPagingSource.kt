package com.nikealarm.nikedrawalarm.data.repository.dataSource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.nikealarm.nikedrawalarm.data.retrofit.RetrofitService
import com.nikealarm.nikedrawalarm.domain.model.ProductInfo
import com.nikealarm.nikedrawalarm.domain.model.getProductFilter
import com.nikealarm.nikedrawalarm.domain.model.translateToProductInfoList

class UpcomingPagingSource(
    private val retrofitService: RetrofitService
) : PagingSource<Int, ProductInfo>() {

    override fun getRefreshKey(state: PagingState<Int, ProductInfo>): Int? {
        return state.anchorPosition?.let {
            state.closestPageToPosition(it)?.prevKey
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ProductInfo> {
        return try {
            val key = params.key ?: 0
            val data = retrofitService.getUpcomingProducts(key)
            val productInfoList = mutableListOf<ProductInfo>()

            data.objects.filter { // 제품들에 관해서만 필터링, Test 제품 걸러내기
                getProductFilter(it)
            }.forEach { filterProduct ->
                // 컬렉션 제품인 경우
                translateToProductInfoList(filterProduct).forEach { productInfo ->
                    if (productInfo.eventDate != 0L) // 이미 출시 된 제품은 필터링 함
                        productInfoList.add(productInfo)
                }
            }

            LoadResult.Page(
                data = productInfoList.toList(),
                prevKey = null,
                nextKey = if (data.objects.isNotEmpty()) key + 50 else null
            )
        } catch (e: Exception) {
            e.printStackTrace()
            LoadResult.Error(e)
        }
    }
}