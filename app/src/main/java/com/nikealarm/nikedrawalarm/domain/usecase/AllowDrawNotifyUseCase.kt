package com.nikealarm.nikedrawalarm.domain.usecase

import com.nikealarm.nikedrawalarm.data.repository.ProductDatabaseRepository
import com.nikealarm.nikedrawalarm.data.repository.ProductRepository
import javax.inject.Inject

class AllowDrawNotifyUseCase @Inject constructor(
    private val databaseRepository: ProductDatabaseRepository,
    private val productRepository: ProductRepository
) {
    suspend operator fun invoke(isAllow: Boolean) {
        if (isAllow) {
            productRepository.setRepeatNewDrawNotify()
        } else {
            productRepository.cancelRepeatNewDrawNotify()
        }

        databaseRepository.setAllowDrawNotification(isAllow)
    }
}