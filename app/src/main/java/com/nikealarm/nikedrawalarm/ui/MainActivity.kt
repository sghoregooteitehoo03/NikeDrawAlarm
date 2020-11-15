package com.nikealarm.nikedrawalarm.ui

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.fragment.findNavController
import com.nikealarm.nikedrawalarm.R
import com.nikealarm.nikedrawalarm.other.Contents
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*

/*
* 자동응모 기능
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
        when(intent?.action) {
            Contents.INTENT_ACTION_GOTO_WEBSITE -> {
                setIntent(intent)
                cancelNotification()
                nav_host_fragment.findNavController().navigate(R.id.action_global_WebFragment)
            }
            Contents.INTENT_ACTION_GOTO_DRAWLIST -> {
                setIntent(intent)
                cancelNotification()
                nav_host_fragment.findNavController().navigate(R.id.action_global_drawListFragment)
            }
            Contents.INTENT_ACTION_GOTO_AUTO_ENTER -> {
                setIntent(intent)
                cancelNotification()
                nav_host_fragment.findNavController().navigate(R.id.action_global_autoEnterFragment)
            }
        }
    }

    private fun cancelNotification() {
        val closeChannelId = intent.getIntExtra(Contents.CHANNEL_ID, -1)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if(closeChannelId != -1) {
            notificationManager.cancel(closeChannelId)
        } else {
            notificationManager.cancelAll()
        }
    }
}