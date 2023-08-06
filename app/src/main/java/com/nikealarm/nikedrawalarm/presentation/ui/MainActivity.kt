package com.nikealarm.nikedrawalarm.presentation.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.plcoding.cryptocurrencyappyt.presentation.ui.theme.NikeDrawAssistant
import dagger.hilt.android.AndroidEntryPoint

/*
* adb shell dumpsys alarm (알림 체크)
* UPCOMING 정보 시 분 초 표시 (서버 필요)
* */

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NikeDrawAssistant {
                Hello()
            }
        }
    }
}

@Composable
fun Hello() {
    Text(text = "Hello World")
}

@Preview
@Composable
fun HelloPreview() {
    NikeDrawAssistant {
        Hello()
    }
}