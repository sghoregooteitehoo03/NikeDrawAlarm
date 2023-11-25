package com.nikealarm.nikedrawalarm.domain.usecase

import com.nikealarm.nikedrawalarm.data.repository.ProductDatabaseRepository
import javax.inject.Inject

class GetNotificationUseCase @Inject constructor(
    private val repository: ProductDatabaseRepository
) {
    operator fun invoke(productId: String) = repository.getNotificationData(productId)
}