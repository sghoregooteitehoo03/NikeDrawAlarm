package com.nikealarm.nikedrawalarm.domain.usecase

import com.nikealarm.nikedrawalarm.data.repository.ProductDatabaseRepository
import com.nikealarm.nikedrawalarm.data.repository.ProductRepository
import com.nikealarm.nikedrawalarm.presentation.settingScreen.ClearProductDialogType
import javax.inject.Inject

class ClearProductUseCase @Inject constructor(
    private val productRepository: ProductRepository,
    private val databaseRepository: ProductDatabaseRepository
) {
    suspend operator fun invoke(type: ClearProductDialogType) {
        when (type) {
            is ClearProductDialogType.ClearLatestDialog -> {
                databaseRepository.clearLatestData()
            }

            is ClearProductDialogType.ClearNotifyDialog -> {
                productRepository.clearNotification()
            }

            is ClearProductDialogType.ClearFavoriteDialog -> {
                databaseRepository.clearFavoriteData()

            }

            else -> {}
        }
    }
}