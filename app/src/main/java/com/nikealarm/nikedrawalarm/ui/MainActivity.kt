package com.nikealarm.nikedrawalarm.ui

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.fragment.findNavController
import androidx.work.WorkManager
import com.nikealarm.nikedrawalarm.R
import com.nikealarm.nikedrawalarm.database.MyDataBase
import com.nikealarm.nikedrawalarm.other.Contents
import com.nikealarm.nikedrawalarm.ui.fragment.ShoesListFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/*
* 위치 버튼 클릭시 이미지 이동(보류)
* 로딩화면 추가
* UI 수정 및 최적화
* 코드 최적화
* */
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
        if(intent?.action == Contents.INTENT_ACTION_GOTO_WEBSITE) {
            setIntent(intent)
            cancelNotification()
            nav_host_fragment.findNavController().navigate(R.id.action_global_mainFragment)
        } else if(intent?.action == Contents.INTENT_ACTION_GOTO_DRAWLIST) {
            setIntent(intent)
            cancelNotification()
            nav_host_fragment.findNavController().navigate(R.id.action_global_drawListFragment)
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