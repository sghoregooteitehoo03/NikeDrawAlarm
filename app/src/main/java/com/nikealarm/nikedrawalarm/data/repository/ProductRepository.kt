package com.nikealarm.nikedrawalarm.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.nikealarm.nikedrawalarm.data.repository.dataSource.ProductPagingSource
import com.nikealarm.nikedrawalarm.data.repository.dataSource.UpcomingPagingSource
import com.nikealarm.nikedrawalarm.data.retrofit.RetrofitService
import com.nikealarm.nikedrawalarm.domain.model.ProductInfo
import com.nikealarm.nikedrawalarm.domain.model.translateToProductInfoList
import com.nikealarm.nikedrawalarm.util.AlarmBuilder
import com.nikealarm.nikedrawalarm.util.Constants
import retrofit2.Retrofit
import javax.inject.Inject

class ProductRepository @Inject constructor(
    private val alarmBuilder: AlarmBuilder,
    private val retrofitBuilder: Retrofit.Builder,
    private val databaseRepository: ProductDatabaseRepository
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

    suspend fun getProductInfo(productId: String, slug: String): ProductInfo {
        val retrofitService = getRetrofitService()
        val productData =
            retrofitService.getProductInfo("seoSlugs%28{slug}%29".replace("{slug}", slug))

        val productObject = productData.objects[0]
        val productInfoList = translateToProductInfoList(productObject)

        return productInfoList.filter { it.productId == productId }[0]
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
        databaseRepository.insertNotificationData(
            productInfo = productInfo,
            triggerTime = triggerTime,
            notificationTime = notificationTime
        )
    }

    suspend fun cancelNotificationProduct(
        productId: String
    ) {
        alarmBuilder.cancelProductAlarm(productId)
        databaseRepository.deleteNotificationData(productId)
    }

    suspend fun clearNotification() {
        val notificationEntities = databaseRepository.getNotificationsData()
        notificationEntities.forEach { entity ->
            cancelNotificationProduct(entity.productId)
        }
    }

    private fun getRetrofitService() =
        retrofitBuilder.baseUrl(Constants.NIKE_API_URL)
            .build()
            .create(RetrofitService::class.java)
}