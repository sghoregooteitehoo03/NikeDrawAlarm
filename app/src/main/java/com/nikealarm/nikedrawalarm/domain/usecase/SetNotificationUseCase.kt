package com.nikealarm.nikedrawalarm.domain.usecase

import com.nikealarm.nikedrawalarm.data.repository.ProductRepository
import com.nikealarm.nikedrawalarm.domain.model.ProductInfo
import javax.inject.Inject

class SetNotificationUseCase @Inject constructor(
    private val repository: ProductRepository
) {

    suspend operator fun invoke(
        productInfo: ProductInfo,
        notificationTime: Long
    ) {
        if (notificationTime != 0L) {
            repository.setNotificationProduct(
                productInfo,
                notificationTime
            )
        } else { // 유저가 알림설정에서 설정 안함을 눌렀을 경우
            repository.cancelNotificationProduct(productInfo)
        }
    }
}