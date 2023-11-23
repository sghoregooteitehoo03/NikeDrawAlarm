package com.nikealarm.nikedrawalarm.presentation.settingScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nikealarm.nikedrawalarm.domain.usecase.ClearProductUseCase
import com.nikealarm.nikedrawalarm.domain.usecase.GetSettingInitValueUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val getSettingInitValueUseCase: GetSettingInitValueUseCase,
    private val clearProductUseCase: ClearProductUseCase,
) : ViewModel() {
    private var dialogType: ClearProductDialogType = ClearProductDialogType.Nothing
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
        clearProductUseCase(dialogType)
    }

    fun setDialogType(type: ClearProductDialogType) {
        dialogType = type
    }

    fun getDialogCategory() = dialogType
}