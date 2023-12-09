package com.nikealarm.nikedrawalarm.presentation.settingScreen

data class SettingUiState(
    val isAllowNotify: Boolean = false,
    val isAllowDrawNotify: Boolean = false,
    val isLoading: Boolean = true
)