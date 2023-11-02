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
        repository.setNotificationProduct(productInfo, notificationTime)
    }
}