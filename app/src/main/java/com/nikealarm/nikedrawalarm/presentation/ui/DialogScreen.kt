package com.nikealarm.nikedrawalarm.presentation.ui

sealed class DialogScreen(isDialogOpen: Boolean) {
    data object DialogDismiss : DialogScreen(false)
    data object DialogSetNotify : DialogScreen(true)
    data object DialogAllowNotify : DialogScreen(true)
    data object DialogSetPermission : DialogScreen(true)
    data object DialogSetPushAlarm : DialogScreen(true)
    data object DialogClearProduct : DialogScreen(true)
}