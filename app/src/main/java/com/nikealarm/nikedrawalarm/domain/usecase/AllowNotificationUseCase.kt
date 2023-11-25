package com.nikealarm.nikedrawalarm.domain.usecase

import com.nikealarm.nikedrawalarm.data.repository.ProductDatabaseRepository
import com.nikealarm.nikedrawalarm.presentation.settingScreen.ClearProductType
import javax.inject.Inject

class AllowNotificationUseCase @Inject constructor(
    private val databaseRepository: ProductDatabaseRepository
) {

    suspend operator fun invoke(isAllow: Boolean) {
        databaseRepository.setAllowNotification(isAllow)
    }
}