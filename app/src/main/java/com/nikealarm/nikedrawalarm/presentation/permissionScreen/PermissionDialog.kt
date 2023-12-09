package com.nikealarm.nikedrawalarm.presentation.permissionScreen

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nikealarm.nikedrawalarm.presentation.ui.DialogFormat
import com.plcoding.cryptocurrencyappyt.presentation.ui.theme.Typography

@Composable
fun PermissionDialog(
    modifier: Modifier = Modifier,
    title: String,
    explain: String,
    onDismissRequest: () -> Unit,
    onClick: () -> Unit
) {
    DialogFormat(
        modifier = modifier,
        title = title,
        content = {
            Text(
                text = explain,
                style = Typography.h5.copy(fontWeight = FontWeight.Normal),
                modifier = Modifier.padding(start = 14.dp, end = 14.dp)
            )
        },
        onDismissRequest = onDismissRequest,
        buttonText = "확인",
        onAllowClick = onClick
    )
}