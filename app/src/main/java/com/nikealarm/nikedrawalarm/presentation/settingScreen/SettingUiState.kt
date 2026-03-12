package com.nikealarm.nikedrawalarm.presentation.settingScreen

import com.nikealarm.nikedrawalarm.presentation.ui.DialogScreen

data class SettingUiState(
    val isAllowNotify: Boolean = false,
    val isAllowDrawNotify: Boolean = false,
    val isLoading: Boolean = true,
    val dialogScreen: DialogScreen = DialogScreen.DialogDismiss
)