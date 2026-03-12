package com.nikealarm.nikedrawalarm.data.repository

import android.content.Context
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.nikealarm.nikedrawalarm.component.work.DrawNotifyWorker
import com.nikealarm.nikedrawalarm.data.repository.dataSource.ProductPagingSource
import com.nikealarm.nikedrawalarm.data.repository.dataSource.UpcomingPagingSource
import com.nikealarm.nikedrawalarm.data.retrofit.RetrofitService
import com.nikealarm.nikedrawalarm.domain.model.ProductInfo
import com.nikealarm.nikedrawalarm.domain.model.translateToProductInfoList
import com.nikealarm.nikedrawalarm.util.AlarmBuilder
import com.nikealarm.nikedrawalarm.util.Constants
import dagger.hilt.android.qualifiers.ApplicationContext
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class ProductRepository @Inject constructor(
    @ApplicationContext private val context: Context,
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
        val workRequest =
            PeriodicWorkRequestBuilder<DrawNotifyWorker>(
                6, TimeUnit.HOURS
            ).build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "DrawNotifyWorker",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }

    fun isExistProductAlarm(productId: String) =
        alarmBuilder.isExistProductAlarm(productId)

    fun isExistRepeatAlarm() =
        alarmBuilder.isExistRepeatAlarm()

    fun cancelNotificationProduct(productId: String) {
        alarmBuilder.cancelProductAlarm(productId)
    }

    fun cancelRepeatNewDrawNotify() {
        WorkManager.getInstance(context).cancelUniqueWork("DrawNotifyWorker")
    }

    fun checkAlarmPermissions() = alarmBuilder.checkPermissions()

    fun getRetrofitService() =
        retrofitBuilder.baseUrl(Constants.NIKE_API_URL)
            .build()
            .create(RetrofitService::class.java)
}