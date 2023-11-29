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
    private val retrofitBuilder: Retrofit.Builder
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

    suspend fun getProductInfo(productId: String, slug: String): ProductInfo? {
        return try {
            val retrofitService = getRetrofitService()
            val productData =
                retrofitService.getProductInfo("seoSlugs%28{slug}%29".replace("{slug}", slug))

            // TODO: 읽어오는 중 버그가 발생하는 상품 존재
            val productObject = productData.objects[0]
            val productInfoList = translateToProductInfoList(productObject)

            productInfoList.filter { it.productId == productId }[0]
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // 알림 설정
    fun setNotificationProduct(productId: String, productUrl: String, triggerTime: Long) {
        alarmBuilder.setProductAlarm(
            triggerTime = triggerTime,
            productId = productId,
            productUrl = productUrl
        )
    }

    fun setRepeatNewDrawNotify() {
        alarmBuilder.setRepeatNewDrawNotify()
    }

    fun cancelNotificationProduct(productId: String) {
        alarmBuilder.cancelProductAlarm(productId)
    }

    fun cancelRepeatNewDrawNotify() {
        alarmBuilder.cancelRepeatNewDrawNotify()
    }

    fun checkAlarmPermissions() = alarmBuilder.checkPermissions()

    fun getRetrofitService() =
        retrofitBuilder.baseUrl(Constants.NIKE_API_URL)
            .build()
            .create(RetrofitService::class.java)
}