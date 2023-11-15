package com.nikealarm.nikedrawalarm.Component

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.nikealarm.nikedrawalarm.Component.work.ProductNotificationWorker
import com.nikealarm.nikedrawalarm.util.Constants

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_BOOT_COMPLETED -> {
                // TODO: 재부팅 및 다양한 상황에서 알람이 지워졌을 경우 다시 설정하는 기능 구현하기
            }
            Constants.INTENT_ACTION_PRODUCT_NOTIFICATION -> {
                val productId = intent.getStringExtra(Constants.INTENT_PRODUCT_ID)
                val notificationWorker = OneTimeWorkRequestBuilder<ProductNotificationWorker>()
                    .setInputData(
                        workDataOf(
                            Constants.INTENT_PRODUCT_ID to productId
                        )
                    )
                    .build()

                WorkManager.getInstance(context)
                    .enqueue(notificationWorker)
            }
        }
    }
}