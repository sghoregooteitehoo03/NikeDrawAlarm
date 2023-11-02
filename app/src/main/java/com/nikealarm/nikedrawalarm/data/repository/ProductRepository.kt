package com.nikealarm.nikedrawalarm.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.nikealarm.nikedrawalarm.data.model.entity.FavoriteEntity
import com.nikealarm.nikedrawalarm.data.model.entity.NotificationEntity
import com.nikealarm.nikedrawalarm.data.repository.dataSource.ProductPagingSource
import com.nikealarm.nikedrawalarm.data.repository.database.ProductDao
import com.nikealarm.nikedrawalarm.data.retrofit.RetrofitService
import com.nikealarm.nikedrawalarm.domain.model.ProductCategory
import com.nikealarm.nikedrawalarm.domain.model.ProductInfo
import com.nikealarm.nikedrawalarm.util.Constants
import retrofit2.Retrofit
import javax.inject.Inject


class ProductRepository @Inject constructor(
    private val retrofitBuilder: Retrofit.Builder,
    private val dao: ProductDao
) {

    fun getPagingProduct(selectedCategory: ProductCategory) = Pager(
        config = PagingConfig(
            pageSize = 50
        )
    ) {
        val retrofitService = getRetrofitService()
        ProductPagingSource(
            retrofitService,
            selectedCategory
        )
    }.flow

    fun getFavoriteData(productId: String) =
        dao.getFavoriteData(productId)

    fun getNotificationData(productId: String) =
        dao.getNotificationData(productId = productId)

    suspend fun insertFavoriteData(productInfo: ProductInfo) {
        val productEntity = productInfo.getProductEntity()
        val favoriteEntity = FavoriteEntity(
            productId = productInfo.productId,
            favoriteDate = System.currentTimeMillis()
        )

        dao.insertProductData(productEntity)
        dao.insertFavoriteData(favoriteEntity)
    }

    suspend fun setNotificationProduct(
        productInfo: ProductInfo,
        notificationTime: Long
    ) {
        insertNotificationData(productInfo, notificationTime)
    }

    suspend fun insertNotificationData(
        productInfo: ProductInfo,
        notificationTime: Long
    ) {
        val productEntity = productInfo.getProductEntity()
        val notificationEntity = NotificationEntity(
            productId = productInfo.productId,
            notificationDate = notificationTime,
            addedDate = System.currentTimeMillis()
        )

        dao.insertProductData(productEntity)
        dao.insertNotificationData(notificationEntity)
    }

    suspend fun deleteFavoriteData(productId: String) {
        dao.deleteFavoriteData(productId)
    }

    suspend fun deleteNotificationData(productId: String) {
        dao.deleteNotificationData(productId)
    }

    private fun getRetrofitService() =
        retrofitBuilder.baseUrl(Constants.NIKE_API_URL)
            .build()
            .create(RetrofitService::class.java)
}