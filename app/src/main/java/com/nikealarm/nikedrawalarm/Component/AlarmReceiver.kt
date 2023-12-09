package com.nikealarm.nikedrawalarm.Component

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.nikealarm.nikedrawalarm.Component.work.DrawNotifyWorker
import com.nikealarm.nikedrawalarm.Component.work.ProductNotificationWorker
import com.nikealarm.nikedrawalarm.Component.work.ResetAlarmWorker
import com.nikealarm.nikedrawalarm.util.Constants

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_BOOT_COMPLETED, Intent.ACTION_MY_PACKAGE_REPLACED -> {
                val resetAlarmWorker = OneTimeWorkRequestBuilder<ResetAlarmWorker>()
                    .build()

                WorkManager.getInstance(context)
                    .enqueue(resetAlarmWorker)
            }

            Constants.INTENT_ACTION_PRODUCT_NOTIFICATION -> { // 제품 알림
                val productId = intent.getStringExtra(Constants.INTENT_PRODUCT_ID)
                val notificationWorker = OneTimeWorkRequestBuilder<ProductNotificationWorker>()
                    .setInputData(
                        workDataOf(
                            Constants.INTENT_PRODUCT_ID to productId
                        )
                    )
                    .build()

                // 알림 설정한 제품에 대한 정보를 Notification 날림
                WorkManager.getInstance(context)
                    .enqueue(notificationWorker)
            }

            Constants.INTENT_ACTION_NEW_DRAW_PRODUCT_NOTIFICATION -> { // Draw 신제품 출시
                val findDrawWorker = OneTimeWorkRequestBuilder<DrawNotifyWorker>()
                    .build()

                // 새로 출시한 Draw 제품이 있으면 Notification 날림
                WorkManager.getInstance(context)
                    .enqueue(findDrawWorker)
            }
        }
    }
}