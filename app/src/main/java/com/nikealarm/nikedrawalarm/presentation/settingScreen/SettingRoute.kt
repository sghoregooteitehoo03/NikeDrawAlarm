package com.nikealarm.nikedrawalarm.presentation.settingScreen

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nikealarm.nikedrawalarm.presentation.ui.DialogScreen

@Composable
fun SettingRoute(
    viewModel: SettingViewModel = hiltViewModel(),
    dialogScreen: DialogScreen,
    openDialog: (DialogScreen) -> Unit,
    onContactEmailClick: () -> Unit,
    onDismiss: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    SettingScreen(
        uiState = uiState,
        onAllowNotifyClick = {},
        onAllowDrawNotifyClick = {},
        onClearProductClick = {
            viewModel.setDialogType(it)
            openDialog(DialogScreen.DialogClearProduct)
        },
        onContactEmailClick = onContactEmailClick
    )

    when (dialogScreen) {
        is DialogScreen.DialogClearProduct -> {
            val explain = when (viewModel.getDialogCategory()) {
                is ClearProductDialogType.ClearLatestDialog -> "최근에 본 제품 목록들을 초기화 하시겠습니까?"
                is ClearProductDialogType.ClearNotifyDialog -> "알림 설정한 제품 목록들을 초기화 하시겠습니까?"
                is ClearProductDialogType.ClearFavoriteDialog -> "좋아요 한 제품 목록들을 초기화 하시겠습니까?"
                else -> ""
            }

            ProductClearDialog(
                modifier = Modifier.fillMaxWidth(),
                explain = explain,
                onDismissRequest = onDismiss,
                onAllowClick = {
                    viewModel.clearProduct()
                    onDismiss()
                },
                onCancelClick = { onDismiss() }
            )
        }

        else -> {}
    }
}