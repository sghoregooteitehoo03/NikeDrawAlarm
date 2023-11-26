package com.nikealarm.nikedrawalarm.presentation.ui

sealed interface DialogScreen {
    data object DialogDismiss : DialogScreen
    data object DialogSetNotify : DialogScreen
    data object DialogAllowNotify : DialogScreen
    data object DialogSetPermission : DialogScreen
    data object DialogSetPushAlarm : DialogScreen
    data object DialogClearProduct : DialogScreen
}