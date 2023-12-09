package com.nikealarm.nikedrawalarm.presentation.settingScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nikealarm.nikedrawalarm.domain.usecase.AllowDrawNotifyUseCase
import com.nikealarm.nikedrawalarm.domain.usecase.AllowNotificationUseCase
import com.nikealarm.nikedrawalarm.domain.usecase.ClearProductUseCase
import com.nikealarm.nikedrawalarm.domain.usecase.GetSettingInitUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val getSettingInitValueUseCase: GetSettingInitUseCase,
    private val clearProductUseCase: ClearProductUseCase,
    private val allowNotificationUseCase: AllowNotificationUseCase,
    private val allowDrawNotifyUseCase: AllowDrawNotifyUseCase
) : ViewModel() {
    private var clearProductType: ClearProductType = ClearProductType.Nothing
    val uiState = getSettingInitValueUseCase { isAllowNotify, isAllowDrawNotify ->
        SettingUiState(
            isAllowNotify = isAllowNotify,
            isAllowDrawNotify = isAllowDrawNotify,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = SettingUiState()
    )

    fun clearProduct() = viewModelScope.launch {
        clearProductUseCase(clearProductType)
        clearProductType = ClearProductType.Nothing
    }

    fun setDialogType(type: ClearProductType) {
        clearProductType = type
    }

    fun allowNotification(isAllow: Boolean) = viewModelScope.launch {
        allowNotificationUseCase(isAllow)
    }

    fun allowDrawNotification(isAllow: Boolean) = viewModelScope.launch {
        allowDrawNotifyUseCase(isAllow)
    }

    fun getDialogCategory() = clearProductType
}