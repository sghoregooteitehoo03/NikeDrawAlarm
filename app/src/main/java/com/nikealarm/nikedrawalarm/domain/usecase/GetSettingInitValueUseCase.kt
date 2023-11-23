package com.nikealarm.nikedrawalarm.domain.usecase

import com.nikealarm.nikedrawalarm.data.repository.ProductDatabaseRepository
import com.nikealarm.nikedrawalarm.presentation.settingScreen.SettingUiState
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class GetSettingInitValueUseCase @Inject constructor(
    private val repository: ProductDatabaseRepository
) {
    operator fun invoke(transform: suspend (Boolean, Boolean) -> SettingUiState) =
        combine(
            repository.getAllowNotification(),
            repository.getAllowDrawNotification(),
            transform
        )
}