package com.nikealarm.nikedrawalarm

import android.app.NotificationManager
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.getSystemService

class MainActivity : AppCompatActivity() {
    companion object {
        const val CHANNEL_ID = "channelId"
        const val DRAW_URL = "drawUrl"

        const val SET_ALARM = "setAlarm"
        const val REQUEST_ALARM_CODE = 2000
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 기존의 알림창이 존재 했을 때 알림창을 제거함
        val closeChannelId = intent.getIntExtra(CHANNEL_ID, -1)
        if(closeChannelId != -1) {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            notificationManager.cancel(closeChannelId)
        }
    }
}