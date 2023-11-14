package com.nikealarm.nikedrawalarm.Component

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.nikealarm.nikedrawalarm.util.Constants

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == "android.intent.action.BOOT_COMPLETED") {

        } else {
            val productId = intent?.getStringExtra(Constants.INTENT_PRODUCT_ID)
            Log.i("Receiver", "prdouctId: $productId")
            // TODO: 알림 표시하는 Workmanager 구현
        }
    }
}