package com.nikealarm.nikedrawalarm.presentation.settingScreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nikealarm.nikedrawalarm.BuildConfig
import com.plcoding.cryptocurrencyappyt.presentation.ui.theme.NikeDrawAssistant
import com.plcoding.cryptocurrencyappyt.presentation.ui.theme.TextGray
import com.plcoding.cryptocurrencyappyt.presentation.ui.theme.Typography

@Composable
fun SettingScreen(
    uiState: SettingUiState,
    onAllowNotifyClick: (Boolean) -> Unit,
    onAllowDrawNotifyClick: (Boolean) -> Unit,
    onClearProductClick: (ClearProductType) -> Unit,
    onContactEmailClick: () -> Unit
) {
    // TODO: 로딩 화면 구현
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        SettingLayout(
            modifier = Modifier.fillMaxWidth(),
            category = "알림",
            paddingValues = PaddingValues(start = 14.dp, end = 14.dp)
        ) { paddingValue ->
            SettingContentWithSwitch(
                modifier = Modifier.fillMaxWidth(),
                isAllow = uiState.isAllowNotify,
                text = "알림 허용",
                paddingValues = paddingValue,
                onContentClick = onAllowNotifyClick
            )
            SettingContent(
                modifier = Modifier.fillMaxWidth(),
                text = "제품 출시/응모 알림",
                paddingValues = paddingValue,
                enabled = uiState.isAllowNotify
            )
            SettingContentWithSwitch(
                modifier = Modifier.fillMaxWidth(),
                isAllow = uiState.isAllowDrawNotify,
                text = "Draw 신제품 출시 알림",
                subText = "새로 출시하는 Draw 제품을 알려드립니다.",
                enabled = uiState.isAllowNotify,
                paddingValues = paddingValue,
                onContentClick = onAllowDrawNotifyClick
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        SettingLayout(
            modifier = Modifier.fillMaxWidth(),
            category = "데이터",
            paddingValues = PaddingValues(start = 14.dp, end = 14.dp)
        ) { paddingValue ->
            SettingContent(
                modifier = Modifier.fillMaxWidth(),
                text = "최근에 본 제품 목록 초기화",
                paddingValues = paddingValue,
                onContentClick = { onClearProductClick(ClearProductType.ClearLatestProduct) }
            )
            SettingContent(
                modifier = Modifier.fillMaxWidth(),
                text = "알림 설정한 제품 목록 초기화",
                paddingValues = paddingValue,
                onContentClick = { onClearProductClick(ClearProductType.ClearNotifyProduct) }
            )
            SettingContent(
                modifier = Modifier.fillMaxWidth(),
                text = "좋아요 한 제품 목록 초기화",
                paddingValues = paddingValue,
                onContentClick = { onClearProductClick(ClearProductType.ClearFavoriteProduct) }
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        SettingLayout(
            modifier = Modifier.fillMaxWidth(),
            category = "기타",
            paddingValues = PaddingValues(start = 14.dp, end = 14.dp)
        ) { paddingValue ->
            SettingContent(
                modifier = Modifier.fillMaxWidth(),
                text = "개발자에게 문의하기",
                paddingValues = paddingValue,
                onContentClick = onContactEmailClick
            )
            SettingContent(
                modifier = Modifier.fillMaxWidth(),
                text = "Ver ${BuildConfig.VERSION_NAME}",
                paddingValues = paddingValue
            )
        }
    }
}

@Composable
fun SettingLayout(
    modifier: Modifier = Modifier,
    category: String,
    paddingValues: PaddingValues,
    content: @Composable (PaddingValues) -> Unit
) {
    Column(modifier = modifier) {
        Text(
            text = category,
            style = Typography.body1.copy(fontWeight = FontWeight.ExtraBold),
            modifier = Modifier.padding(paddingValues)
        )
        Spacer(modifier = Modifier.height(4.dp))
        content(paddingValues)
    }
}

@Composable
fun SettingContent(
    modifier: Modifier = Modifier,
    text: String,
    subText: String = "",
    enabled: Boolean = true,
    paddingValues: PaddingValues = PaddingValues(0.dp),
    onContentClick: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .height(50.dp)
            .then(
                if (enabled) {
                    modifier.clickable { onContentClick() }
                } else {
                    modifier.alpha(0.4f)
                }
            )
            .padding(paddingValues),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = text,
            style = Typography.h5.copy(fontWeight = FontWeight.Normal)
        )
        if (subText.isNotEmpty()) {
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subText,
                style = Typography.body1.copy(color = TextGray)
            )
        }
    }
}

@Composable
fun SettingContentWithSwitch(
    modifier: Modifier = Modifier,
    isAllow: Boolean,
    text: String,
    subText: String = "",
    enabled: Boolean = true,
    paddingValues: PaddingValues = PaddingValues(0.dp),
    onContentClick: (Boolean) -> Unit = {}
) {
    Box(
        modifier = modifier
            .height(50.dp)
            .then(
                if (enabled) {
                    modifier
                } else {
                    modifier.alpha(0.4f)
                }
            )
    ) {
        SettingContent(
            text = text,
            subText = subText,
            enabled = enabled,
            paddingValues = paddingValues,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterStart),
            onContentClick = { onContentClick(!isAllow) }
        )
        Switch(
            checked = isAllow,
            onCheckedChange = { onContentClick(!isAllow) },
            enabled = enabled,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(paddingValues)
        )
    }
}

@Preview
@Composable
fun SettingItemPreview() {
    NikeDrawAssistant {
        var isAllow by remember { mutableStateOf(false) }

        SettingLayout(
            modifier = Modifier.fillMaxWidth(),
            category = "알림",
            paddingValues = PaddingValues(start = 14.dp, end = 14.dp)
        ) {
            SettingContent(
                modifier = Modifier.fillMaxWidth(),
                text = "제품 출시/응모 알림",
                paddingValues = PaddingValues(start = 14.dp, end = 14.dp)
            )
            SettingContentWithSwitch(
                modifier = Modifier.fillMaxWidth(),
                isAllow = isAllow,
                text = "Draw 신제품 출시 알림",
                subText = "새로 출시하는 Draw 제품을 알려드립니다.",
                enabled = true,
                paddingValues = PaddingValues(start = 14.dp, end = 14.dp),
                onContentClick = { isAllow = !isAllow }
            )
        }
    }
}