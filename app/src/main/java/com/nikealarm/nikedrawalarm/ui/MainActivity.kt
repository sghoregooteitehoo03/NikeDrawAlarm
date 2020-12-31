package com.nikealarm.nikedrawalarm.ui

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.navigation.findNavController
import com.nikealarm.nikedrawalarm.R
import com.nikealarm.nikedrawalarm.other.Contents
import com.nikealarm.nikedrawalarm.other.CustomTabsBuilder
import dagger.hilt.android.AndroidEntryPoint

/*
* adb shell dumpsys alarm (알림 체크)
* UPCOMING 정보 시 분 초 표시 (서버 필요)
* 진행중인 상품 알려주기
* UI 수정 및 최적화
* 코드 최적화
* */
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 기존의 알림창이 존재 했을 때 알림창을 제거함
        ifNeedToMoveFragment(intent)
        cancelNotification()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        ifNeedToMoveFragment(intent)
    }

    private fun ifNeedToMoveFragment(intent: Intent?) {
        when (intent?.action) {
            Contents.INTENT_ACTION_GOTO_WEBSITE -> {
                setIntent(intent)
                cancelNotification()

                val url = intent.getStringExtra(Contents.DRAW_URL)
                    ?: "https://www.nike.com/kr/launch/"
                val builder = CustomTabsBuilder().getBuilder()
                builder.build()
                    .launchUrl(this, Uri.parse(url))
            }
            Contents.INTENT_ACTION_GOTO_DRAWLIST -> {
                setIntent(intent)
                cancelNotification()
                findNavController(R.id.nav_host_fragment).navigate(R.id.action_global_drawListFragment)
            }
        }
    }

    private fun cancelNotification() {
        val closeChannelId = intent.getIntExtra(Contents.CHANNEL_ID, -1)
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (closeChannelId != -1) {
            notificationManager.cancel(closeChannelId)
        } else {
            notificationManager.cancelAll()
        }
    }
}