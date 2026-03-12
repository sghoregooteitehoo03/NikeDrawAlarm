package com.nikealarm.nikedrawalarm.presentation.settingScreen

import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nikealarm.nikedrawalarm.R
import com.nikealarm.nikedrawalarm.presentation.ui.DialogScreen
import com.nikealarm.nikedrawalarm.util.Constants
import kotlinx.coroutines.flow.collectLatest

@Composable
fun SettingRoute(viewModel: SettingViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(key1 = viewModel.uiEvent) {
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                is SettingUiEvent.ClickAllowNotify -> {
                    if (event.isAllow) {
                        checkPushAlarm(
                            openDialog = viewModel::setDialogScreen,
                            allowNotification = viewModel::allowNotification,
                            context = context
                        )
                    } else {
                        viewModel.allowNotification(false)
                    }
                }

                is SettingUiEvent.ClickAllowDrawFind -> {
                    viewModel.allowDrawNotification(event.isAllow)
                }

                is SettingUiEvent.ClickClearProduct -> {
                    viewModel.setClearType(event.clearProductType)
                    viewModel.setDialogScreen(DialogScreen.DialogClearProduct)
                }

                is SettingUiEvent.ClickContactEmail -> {
                    moveEmailIntent(context)
                }
            }
        }
    }

    SettingScreen(
        uiState = uiState,
        onEvent = viewModel::handleEvent
    )

    when (uiState.dialogScreen) {
        is DialogScreen.DialogClearProduct -> {
            val explain = when (viewModel.getDialogCategory()) {
                is ClearProductType.ClearLatestProduct -> "최근에 본 제품 목록들을 초기화 하시겠습니까?"
                is ClearProductType.ClearNotifyProduct -> "알림 설정한 제품 목록들을 초기화 하시겠습니까?"
                is ClearProductType.ClearFavoriteProduct -> "좋아요 한 제품 목록들을 초기화 하시겠습니까?"
                else -> ""
            }

            ProductClearDialog(
                modifier = Modifier.fillMaxWidth(),
                explain = explain,
                onDismissRequest = { viewModel.setDialogScreen(DialogScreen.DialogDismiss) },
                onAllowClick = {
                    viewModel.clearProduct()
                    viewModel.setDialogScreen(DialogScreen.DialogDismiss)
                },
                onCancelClick = { viewModel.setDialogScreen(DialogScreen.DialogDismiss) }
            )
        }

        is DialogScreen.DialogSetPushAlarm -> {
            InformationDialog(
                modifier = Modifier.fillMaxWidth(),
                title = "푸쉬알림 설정",
                explain = stringResource(id = R.string.set_notification_explain),
                buttonText = "확인",
                onDismissRequest = { viewModel.setDialogScreen(DialogScreen.DialogDismiss) },
                onAllowClick = {
                    moveSettingIntent(
                        context = context,
                        action = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            Settings.ACTION_APP_NOTIFICATION_SETTINGS
                        } else {
                            "android.settings.APP_NOTIFICATION_SETTINGS"
                        }
                    )
                    viewModel.setDialogScreen(DialogScreen.DialogDismiss)
                }
            )
        }

        is DialogScreen.DialogSetPermission -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                InformationDialog(
                    modifier = Modifier.fillMaxWidth(),
                    title = "권한 설정",
                    explain = stringResource(id = R.string.permission_explain),
                    buttonText = "확인",
                    onDismissRequest = { viewModel.setDialogScreen(DialogScreen.DialogDismiss) },
                    onAllowClick = {
                        moveSettingIntent(context, Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                        viewModel.setDialogScreen(DialogScreen.DialogDismiss)
                    }
                )
            } else {
                viewModel.setDialogScreen(DialogScreen.DialogDismiss)
            }
        }

        else -> {}
    }
}

private fun checkPushAlarm(
    openDialog: (DialogScreen) -> Unit,
    allowNotification: (Boolean) -> Unit,
    context: Context
) {
    // 푸쉬 알림설정이 활성화 되어있는 경우
    if (NotificationManagerCompat.from(context).areNotificationsEnabled()) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            checkAlarmPermission(
                openDialog,
                allowNotification,
                context
            ) // Schedule 알림 권한이 설정 되어 있는 경우
        } else {
            allowNotification(true)
        }
    } else {
        openDialog(DialogScreen.DialogSetPushAlarm)
    }
}

private fun moveEmailIntent(context: Context) {
    // 이메일로 바로 이동
    val intent = Intent(Intent.ACTION_SENDTO).apply {
        val email = arrayOf(Constants.DEVELOPER_EMAIL)
        data = Uri.parse("mailto:")
        putExtra(Intent.EXTRA_EMAIL, email)
    }

    if (intent.resolveActivity(context.packageManager) != null) {
        context.startActivity(intent)
    }
}

private fun moveSettingIntent(context: Context, action: String) {
    val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        Intent(action).apply {
            this.putExtra(
                Settings.EXTRA_APP_PACKAGE,
                context.packageName
            )
        }
    } else {
        Intent().apply {
            this.action = action
            putExtra("app_package", context.packageName)
            putExtra("app_uid", context.applicationInfo?.uid)
        }
    }

    context.startActivity(intent)
}

@RequiresApi(Build.VERSION_CODES.S)
private fun checkAlarmPermission(
    openDialog: (DialogScreen) -> Unit,
    allowNotification: (Boolean) -> Unit,
    context: Context
) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    if (alarmManager.canScheduleExactAlarms()) { // 권한이 활성화 되어있는지 확인
        allowNotification(true)
    } else {
        openDialog(DialogScreen.DialogSetPermission)
    }
}