package com.nikealarm.core.domain.usecase

import com.nikealarm.core.common.Result
import com.nikealarm.core.common.getOrThrow
import com.nikealarm.core.common.runCatching
import com.nikealarm.core.domain.repository.DatabaseRepository
import com.nikealarm.core.domain.repository.ProductRepository
import com.nikealarm.core.model.ProductInfo
import javax.inject.Inject

class SetNotificationUseCase @Inject constructor(
    private val productRepository: ProductRepository,
    private val databaseRepository: DatabaseRepository
) {

    suspend operator fun invoke(
        productInfo: ProductInfo,
        notificationTime: Long
    ): Result<Unit> {
        return runCatching {
            if (notificationTime != 0L) {
                val triggerTime = productInfo.eventDate.minus(notificationTime)

                productRepository.setNotificationProduct(
                    productInfo.productId,
                    productUrl = productInfo.url,
                    triggerTime
                ).getOrThrow()
                databaseRepository.insertNotificationData(
                    productInfo = productInfo,
                    triggerTime = triggerTime,
                    notificationTime = notificationTime
                ).getOrThrow()
            } else { // 유저가 알림설정에서 설정 안함을 눌렀을 경우
                productRepository.cancelNotificationProduct(productInfo.productId).getOrThrow()
                databaseRepository.deleteNotificationData(productInfo.productId).getOrThrow()
            }
        }
    }
}