package com.nikealarm.nikedrawalarm.domain.usecase

import com.nikealarm.nikedrawalarm.data.repository.ProductDatabaseRepository
import com.nikealarm.nikedrawalarm.data.repository.ProductRepository
import com.nikealarm.nikedrawalarm.domain.model.ProductInfo
import javax.inject.Inject

class SetNotificationUseCase @Inject constructor(
    private val productRepository: ProductRepository,
    private val databaseRepository: ProductDatabaseRepository
) {

    suspend operator fun invoke(
        productInfo: ProductInfo,
        notificationTime: Long
    ) {
        if (notificationTime != 0L) {
            val triggerTime = productInfo.eventDate.minus(notificationTime)

            productRepository.setNotificationProduct(
                productInfo.productId,
                productUrl = productInfo.url,
                triggerTime
            )
            databaseRepository.insertNotificationData(
                productInfo = productInfo,
                triggerTime = triggerTime,
                notificationTime = notificationTime
            )
        } else { // 유저가 알림설정에서 설정 안함을 눌렀을 경우
            productRepository.cancelNotificationProduct(productInfo.productId)
            databaseRepository.deleteNotificationData(productInfo.productId)
        }
    }
}