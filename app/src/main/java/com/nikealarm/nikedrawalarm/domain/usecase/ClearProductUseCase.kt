package com.nikealarm.nikedrawalarm.domain.usecase

import com.nikealarm.nikedrawalarm.data.repository.ProductDatabaseRepository
import com.nikealarm.nikedrawalarm.data.repository.ProductRepository
import com.nikealarm.nikedrawalarm.presentation.settingScreen.ClearProductType
import javax.inject.Inject

class ClearProductUseCase @Inject constructor(
    private val productRepository: ProductRepository,
    private val databaseRepository: ProductDatabaseRepository
) {
    suspend operator fun invoke(type: ClearProductType) {
        when (type) {
            is ClearProductType.ClearLatestProduct -> {
                databaseRepository.clearLatestData()
            }

            is ClearProductType.ClearNotifyProduct -> {
                val notificationEntities = databaseRepository.getNotificationsData()

                notificationEntities.forEach { notificationEntity ->
                    productRepository.cancelNotificationProduct(notificationEntity.productId)
                    databaseRepository.deleteNotificationData(notificationEntity.productId)
                }
            }

            is ClearProductType.ClearFavoriteProduct -> {
                databaseRepository.clearFavoriteData()

            }

            else -> {}
        }
    }
}