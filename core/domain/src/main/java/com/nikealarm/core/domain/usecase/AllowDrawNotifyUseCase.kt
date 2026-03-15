package com.nikealarm.core.domain.usecase

import com.nikealarm.core.common.Result
import com.nikealarm.core.common.getOrThrow
import com.nikealarm.core.common.runCatching
import com.nikealarm.core.domain.repository.DatabaseRepository
import com.nikealarm.core.domain.repository.ProductRepository
import javax.inject.Inject

class AllowDrawNotifyUseCase @Inject constructor(
    private val databaseRepository: DatabaseRepository,
    private val productRepository: ProductRepository
) {
    suspend operator fun invoke(isAllow: Boolean): Result<Unit> {
        return runCatching {
            if (isAllow) {
                productRepository.setRepeatNewDrawNotify().getOrThrow()
            } else {
                productRepository.cancelRepeatNewDrawNotify().getOrThrow()
            }

            databaseRepository.setAllowDrawNotification(isAllow).getOrThrow()
        }
    }
}