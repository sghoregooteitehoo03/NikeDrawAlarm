package com.nikealarm.nikedrawalarm.other

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import com.nikealarm.nikedrawalarm.component.MyAlarmReceiver
import com.nikealarm.nikedrawalarm.ui.MainActivity

class AlarmBuilder(private val context: Context) {
    private lateinit var alarmIntent: Intent

    fun setIntent(_action: String, _extras: Bundle) {
        alarmIntent = Intent(context, MyAlarmReceiver::class.java).apply {
            action = _action
            putExtras(_extras)
        }
    }

    fun setIntent(_action: String) {
        alarmIntent = Intent(context, MyAlarmReceiver::class.java).apply {
            action = _action
        }
    }

    fun setAlarm(timeTrigger: Long, requestCode: Int) {
        val alarmPendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            alarmIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val alarmManager =
            context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            alarmManager.setExactAndAllowWhileIdle(
//                AlarmManager.RTC_WAKEUP,
//                timeTrigger,
//                alarmPendingIntent
//            )
            val actionIntent = Intent(context, MainActivity::class.java)
            val actionPendingIntent = PendingIntent.getActivity(
                context,
                requestCode,
                actionIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )

            alarmManager.setAlarmClock(
                AlarmManager.AlarmClockInfo(timeTrigger, actionPendingIntent),
                alarmPendingIntent
            )
        } else {
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                timeTrigger,
                alarmPendingIntent
            )
        }
        Log.i("SetAlarm", "동작")
    }

    fun removeAlarm(requestCode: Int) {
        if (checkExistAlarm(requestCode)) {
            val alarmPendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                alarmIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )

            val alarmManager =
                context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            alarmManager.cancel(alarmPendingIntent)
            alarmPendingIntent.cancel()
            Log.i("RemoveAlarm", "동작")
        }
    }

    private fun checkExistAlarm(requestCode: Int): Boolean {
        val alarmPendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            alarmIntent,
            PendingIntent.FLAG_NO_CREATE
        )

        return alarmPendingIntent != null
    }
}