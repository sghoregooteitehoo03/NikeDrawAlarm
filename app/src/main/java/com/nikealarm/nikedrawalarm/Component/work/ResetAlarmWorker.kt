package com.nikealarm.nikedrawalarm.Component.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.nikealarm.nikedrawalarm.data.repository.ProductDatabaseRepository
import com.nikealarm.nikedrawalarm.data.repository.ProductRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first

@HiltWorker
class ResetAlarmWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val databaseRepository: ProductDatabaseRepository,
    private val productRepository: ProductRepository
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val isAllowNotify = databaseRepository.getAllowNotification().first()

        // 알림 허용이 되어 있지 않거나 권한이 설정되어 있지 않으면 fail 반환
        if (!isAllowNotify || !productRepository.checkAlarmPermissions())
            return Result.failure()

        // 드로우 신제품 출시 알림이 설정되어 있으면
        val isAllowDrawNotify = databaseRepository.getAllowDrawNotification().first()
        if (isAllowDrawNotify) {
            productRepository.setRepeatNewDrawNotify()
        }

        val notificationProducts = databaseRepository.getNotifyProductsData()
        notificationProducts.first()
            .forEach { entity ->
                // 해제된 제품 알림들을 다시 설정해줌
                productRepository.setNotificationProduct(
                    productId = entity.productEntity.productId,
                    productUrl = entity.productEntity.url,
                    triggerTime = entity.notificationEntity.triggerTime
                )
            }

        return Result.success()
    }

}