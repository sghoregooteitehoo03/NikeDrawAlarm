package com.nikealarm.nikedrawalarm.presentation.settingScreen

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.nikealarm.nikedrawalarm.presentation.ui.DialogWithCancelFormat
import com.plcoding.cryptocurrencyappyt.presentation.ui.theme.Typography

@Composable
fun ProductClearDialog(
    modifier: Modifier = Modifier,
    explain: String,
    onDismissRequest: () -> Unit,
    onAllowClick: () -> Unit,
    onCancelClick: () -> Unit
) {
    DialogWithCancelFormat(
        modifier = modifier,
        title = "목록 초기화",
        content = {
            Text(
                text = explain,
                style = Typography.h5.copy(fontWeight = FontWeight.Normal),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(
                    start = 28.dp,
                    end = 28.dp,
                    top = 48.dp,
                    bottom = 48.dp
                )
            )
        },
        onDismissRequest = onDismissRequest,
        allowText = "확인",
        cancelText = "취소",
        onAllowClick = onAllowClick,
        onCancelClick = onCancelClick
    )
}

sealed class ClearProductDialogType() {
    data object Nothing : ClearProductDialogType()
    data object ClearLatestDialog : ClearProductDialogType()
    data object ClearNotifyDialog : ClearProductDialogType()
    data object ClearFavoriteDialog : ClearProductDialogType()
}