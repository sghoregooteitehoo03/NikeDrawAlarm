package com.nikealarm.nikedrawalarm.presentation.settingScreen

sealed interface SettingUiEvent {
    data class ClickAllowNotify(val isAllow: Boolean) : SettingUiEvent
    data class ClickAllowDrawFind(val isAllow: Boolean) : SettingUiEvent
    data class ClickClearProduct(val clearProductType: ClearProductType) : SettingUiEvent
    data object ClickContactEmail : SettingUiEvent
}