package com.nikealarm.core.domain.usecase

import com.nikealarm.core.common.Result
import com.nikealarm.core.common.getOrThrow
import com.nikealarm.core.common.runCatching
import com.nikealarm.core.domain.repository.DatabaseRepository
import com.nikealarm.core.domain.repository.ProductRepository
import com.nikealarm.core.model.ClearProductType
import javax.inject.Inject

class ClearProductUseCase @Inject constructor(
    private val databaseRepository: DatabaseRepository,
    private val productRepository: ProductRepository
) {
    suspend operator fun invoke(type: ClearProductType): Result<Unit> {
        return runCatching {
            when (type) {
                is ClearProductType.ClearLatestProduct -> {
                    databaseRepository.clearLatestData().getOrThrow()
                }

                is ClearProductType.ClearNotifyProduct -> {
                    val notificationEntities =
                        databaseRepository.getNotificationsData().getOrThrow()

                    notificationEntities.forEach { notificationEntity ->
                        productRepository.cancelNotificationProduct(notificationEntity.productId)
                            .getOrThrow()
                        databaseRepository.deleteNotificationData(notificationEntity.productId)
                            .getOrThrow()
                    }
                }

                is ClearProductType.ClearFavoriteProduct -> {
                    databaseRepository.clearFavoriteData().getOrThrow()
                }
            }
        }
    }
}