package com.nikealarm.nikedrawalarm.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.nikealarm.nikedrawalarm.data.model.entity.FavoriteEntity
import com.nikealarm.nikedrawalarm.data.model.entity.LatestEntity
import com.nikealarm.nikedrawalarm.data.model.entity.NotificationEntity
import com.nikealarm.nikedrawalarm.data.repository.dataSource.JoinedProductPagingSource
import com.nikealarm.nikedrawalarm.data.repository.dataSource.ProductPagingSource
import com.nikealarm.nikedrawalarm.data.repository.dataSource.UpcomingPagingSource
import com.nikealarm.nikedrawalarm.data.repository.database.ProductDao
import com.nikealarm.nikedrawalarm.data.retrofit.RetrofitService
import com.nikealarm.nikedrawalarm.domain.model.JoinedProductCategory
import com.nikealarm.nikedrawalarm.domain.model.ProductInfo
import com.nikealarm.nikedrawalarm.domain.model.translateToProductInfoList
import com.nikealarm.nikedrawalarm.util.AlarmBuilder
import com.nikealarm.nikedrawalarm.util.Constants
import retrofit2.Retrofit
import javax.inject.Inject

// TODO: 데이터베이스만 관리하는 Repository 만들어서 분리하기
class ProductRepository @Inject constructor(
    private val alarmBuilder: AlarmBuilder,
    private val retrofitBuilder: Retrofit.Builder,
    private val dao: ProductDao
) {

    fun getPagingProducts(isUpcoming: Boolean = false) = Pager(
        config = PagingConfig(pageSize = 50)
    ) {
        val retrofitService = getRetrofitService()
        ProductPagingSource(
            retrofitService,
            isUpcoming
        )
    }.flow

    fun getPagingUpcoming() = Pager(
        config = PagingConfig(pageSize = 50)
    ) {
        val retrofitService = getRetrofitService()
        UpcomingPagingSource(retrofitService)
    }.flow

    fun getPagingJoinedProduct(joinedCategory: JoinedProductCategory) = Pager(
        config = PagingConfig(20)
    ) {
        JoinedProductPagingSource(dao, joinedCategory)
    }.flow

    suspend fun getProductInfo(productId: String, slug: String): ProductInfo {
        val retrofitService = getRetrofitService()
        val productData =
            retrofitService.getProductInfo("seoSlugs%28{slug}%29".replace("{slug}", slug))

        val productObject = productData.objects[0]
        val productInfoList = translateToProductInfoList(productObject)

        return productInfoList.filter { it.productId == productId }[0]
    }

    suspend fun getProductData(productId: String) =
        dao.getProductData(productId)

    fun getFavoriteData(productId: String) =
        dao.getFavoriteData(productId)

    fun getNotificationData(productId: String) =
        dao.getNotificationData(productId = productId)

    fun getLatestProductsData(limit: Int) =
        dao.getLatestProductsData(limit)

    fun getNotifyProductsData(limit: Int) =
        dao.getNotifyProductsData(limit)

    fun getFavoriteProductsData(limit: Int) =
        dao.getFavoriteProductsData(limit)

    suspend fun insertFavoriteData(productInfo: ProductInfo) {
        val productEntity = productInfo.getProductEntity()
        val favoriteEntity = FavoriteEntity(
            productId = productInfo.productId,
            favoriteDate = System.currentTimeMillis()
        )

        dao.insertProductData(productEntity)
        dao.insertFavoriteData(favoriteEntity)
    }

    // 알림 설정
    suspend fun setNotificationProduct(
        productInfo: ProductInfo,
        notificationTime: Long
    ) {
        val triggerTime = productInfo.eventDate.minus(notificationTime)

        alarmBuilder.setProductAlarm(
            triggerTime = triggerTime,
            productId = productInfo.productId
        )
        insertNotificationData(
            productInfo = productInfo,
            triggerTime = triggerTime,
            notificationTime = notificationTime
        )
    }

    suspend fun cancelNotificationProduct(
        productInfo: ProductInfo
    ) {
        alarmBuilder.cancelProductAlarm(productInfo.productId)
        deleteNotificationData(productInfo.productId)
    }

    suspend fun insertNotificationData(
        productInfo: ProductInfo,
        triggerTime: Long,
        notificationTime: Long
    ) {
        val productEntity = productInfo.getProductEntity()
        val notificationEntity = NotificationEntity(
            productId = productInfo.productId,
            triggerTime = triggerTime,
            notificationDate = notificationTime,
            addedDate = System.currentTimeMillis()
        )

        dao.insertProductData(productEntity)
        dao.insertNotificationData(notificationEntity)
    }

    suspend fun insertLatestData(productInfo: ProductInfo) {
        val productEntity = productInfo.getProductEntity()
        val latestEntity =
            LatestEntity(productId = productInfo.productId, latestDate = System.currentTimeMillis())

        dao.insertProductData(productEntity)
        dao.insertLatestData(latestEntity)
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