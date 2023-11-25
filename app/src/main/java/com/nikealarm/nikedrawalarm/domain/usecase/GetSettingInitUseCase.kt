package com.nikealarm.nikedrawalarm.domain.usecase

import com.nikealarm.nikedrawalarm.data.repository.ProductDatabaseRepository
import com.nikealarm.nikedrawalarm.presentation.settingScreen.SettingUiState
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class GetSettingInitUseCase @Inject constructor(
    private val databaseRepository: ProductDatabaseRepository,
    private val getAllowNotifyUseCase: GetAllowNotifyUseCase
) {
    operator fun invoke(transform: suspend (Boolean, Boolean) -> SettingUiState) =
        combine(
            getAllowNotifyUseCase(),
            databaseRepository.getAllowDrawNotification(),
            transform
        )
}