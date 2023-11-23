package com.nikealarm.nikedrawalarm.domain.usecase

import com.nikealarm.nikedrawalarm.data.repository.ProductRepository
import javax.inject.Inject

class ClearNotifyProductUseCase @Inject constructor(
    private val repository: ProductRepository
) {
    suspend operator fun invoke() {
        repository.clearNotification()
    }
}