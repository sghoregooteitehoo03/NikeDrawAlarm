package com.nikealarm.nikedrawalarm.data.repository.dataSource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.nikealarm.nikedrawalarm.data.repository.database.ProductDao
import com.nikealarm.nikedrawalarm.domain.model.JoinedProduct
import com.nikealarm.nikedrawalarm.domain.model.JoinedProductCategory
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Locale

class JoinedProductPagingSource(
    private val dao: ProductDao,
    private val joinedCategory: JoinedProductCategory
) : PagingSource<Int, JoinedProduct>() {
    override fun getRefreshKey(state: PagingState<Int, JoinedProduct>): Int? {
        return state.anchorPosition
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, JoinedProduct> {
        return try {
            val offset = params.key ?: 0
            val limit = offset + 20
            val joinedProduct = when (joinedCategory) {
                JoinedProductCategory.LatestProduct -> {
                    val product = dao.getLatestProductsPageData(limit, offset)
                    product.map {
                        JoinedProduct(
                            productEntity = it.productEntity,
                            explains = DecimalFormat("₩#,###").format(it.productEntity.price),
                        )
                    }
                }

                JoinedProductCategory.NotifyProduct -> {
                    val product = dao.getNotifyProductsPageData(limit, offset)
                    product.map {
                        JoinedProduct(
                            productEntity = it.productEntity,
                            explains = SimpleDateFormat(
                                if (it.notificationEntity.notificationDate >= 3600000L) {
                                    "출시 h시간 전에 알림"
                                } else {
                                    "출시 m분 전에 알림"
                                },
                                Locale.KOREA
                            )
                                .format(
                                    it.notificationEntity.notificationDate.minus(
                                        32400000L
                                    )
                                ),
                        )
                    }
                }

                JoinedProductCategory.FavoriteProduct -> {
                    val product = dao.getFavoriteProductsPageData(limit, offset)
                    product.map {
                        JoinedProduct(
                            productEntity = it.productEntity,
                            explains = SimpleDateFormat(
                                "yy.MM.dd 추가 됨",
                                Locale.KOREA
                            )
                                .format(
                                    it.favoriteEntity.favoriteDate.minus(
                                        32400000L
                                    )
                                )
                        )
                    }
                }
            }

            if (joinedProduct.isEmpty())
                throw NullPointerException()

            LoadResult.Page(
                data = joinedProduct,
                prevKey = null,
                nextKey = limit
            )
        } catch (e: Exception) {
            e.printStackTrace()
            LoadResult.Error(e)
        }
    }
}