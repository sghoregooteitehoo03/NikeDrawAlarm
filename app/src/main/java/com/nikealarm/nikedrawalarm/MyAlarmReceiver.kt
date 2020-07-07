package com.nikealarm.nikedrawalarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import java.util.*

class MyAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
        reSetAlarm(context, intent.getLongExtra(MainActivity.SET_ALARM, 0))

        Log.i("Check", "동작")

        val parsingWorkRequest = OneTimeWorkRequestBuilder<ParsingWorker>()
            .build()
        WorkManager.getInstance(context).enqueue(parsingWorkRequest)
    }

    // 알람 재설정
    private fun reSetAlarm(mContext: Context, timeTrigger: Long) {
        if (timeTrigger != 0.toLong()) {
            val mAlarmManager = mContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            val reIntent = Intent(mContext, MyAlarmReceiver::class.java).apply {
                putExtra(MainActivity.SET_ALARM, timeTrigger + 86400000)
            }

            val alarmPendingIntent = PendingIntent.getBroadcast(
                mContext,
                MainActivity.REQUEST_ALARM_CODE,
                reIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mAlarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    timeTrigger,
                    alarmPendingIntent
                )
            }
        }
    }
}
