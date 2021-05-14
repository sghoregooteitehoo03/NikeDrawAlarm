package com.nikealarm.nikedrawalarm.ui

import android.app.NotificationManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.navigation.findNavController
import com.nikealarm.nikedrawalarm.R
import com.nikealarm.nikedrawalarm.other.Contents
import com.nikealarm.nikedrawalarm.other.CustomTabsBuilder
import dagger.hilt.android.AndroidEntryPoint

/*
* adb shell dumpsys alarm (알림 체크)
* UPCOMING 정보 시 분 초 표시 (서버 필요)
* */

// TODO:
//  1. 응모 여러개 있을 때 두번째부터 응모 안되는 버그 수정 ㅁ
//  2. 데이터 이용시 응모 안되는 버그 수정 O
//  3. 알림 작동 안되는 버그 수정 ㅁ
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
                try {
                    val builder = CustomTabsBuilder().getBuilder()
                    builder.build()
                        .launchUrl(this, Uri.parse(url))
                } catch (e: Exception) {
                    Toast.makeText(this, "크롬 브라우저가 존재하지 않습니다.", Toast.LENGTH_SHORT)
                        .show()
                }
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