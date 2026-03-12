package com.nikealarm.nikedrawalarm.presentation.settingScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nikealarm.nikedrawalarm.domain.usecase.AllowDrawNotifyUseCase
import com.nikealarm.nikedrawalarm.domain.usecase.AllowNotificationUseCase
import com.nikealarm.nikedrawalarm.domain.usecase.ClearProductUseCase
import com.nikealarm.nikedrawalarm.domain.usecase.GetSettingInitUseCase
import com.nikealarm.nikedrawalarm.presentation.ui.DialogScreen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    getSettingInitValueUseCase: GetSettingInitUseCase,
    private val clearProductUseCase: ClearProductUseCase,
    private val allowNotificationUseCase: AllowNotificationUseCase,
    private val allowDrawNotifyUseCase: AllowDrawNotifyUseCase
) : ViewModel() {
    private var clearProductType: ClearProductType = ClearProductType.Nothing
    private val _uiState = MutableStateFlow(SettingUiState())
    private val _uiEvent = MutableSharedFlow<SettingUiEvent>()

    val uiState = _uiState.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        initialValue = SettingUiState()
    )
    val uiEvent = _uiEvent.shareIn(
        viewModelScope,
        SharingStarted.Eagerly
    )

    init {
        viewModelScope.launch {
            getSettingInitValueUseCase { isAllowNotify, isAllowDrawNotify ->
                _uiState.update {
                    it.copy(
                        isAllowNotify = isAllowNotify,
                        isAllowDrawNotify = isAllowDrawNotify,
                        isLoading = false
                    )
                }
            }.collect()
        }
    }

    fun handleEvent(event: SettingUiEvent) = viewModelScope.launch {
        _uiEvent.emit(event)
    }

    fun clearProduct() = viewModelScope.launch {
        clearProductUseCase(clearProductType)
        clearProductType = ClearProductType.Nothing
    }

    fun setClearType(type: ClearProductType) {
        clearProductType = type
    }

    fun setDialogScreen(dialogScreen: DialogScreen) {
        _uiState.update {
            it.copy(dialogScreen = dialogScreen)
        }
    }

    fun allowNotification(isAllow: Boolean) = viewModelScope.launch {
        allowNotificationUseCase(isAllow)
    }

    fun allowDrawNotification(isAllow: Boolean) = viewModelScope.launch {
        allowDrawNotifyUseCase(isAllow)
    }

    fun getDialogCategory() = clearProductType
}