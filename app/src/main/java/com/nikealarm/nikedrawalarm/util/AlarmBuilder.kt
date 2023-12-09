package com.nikealarm.nikedrawalarm.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.TaskStackBuilder
import androidx.core.net.toUri
import com.nikealarm.nikedrawalarm.Component.AlarmReceiver
import com.nikealarm.nikedrawalarm.presentation.ui.MainActivity
import java.util.Calendar

class AlarmBuilder(private val context: Context) {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    // 알림 존재여부 확인
    fun isExistProductAlarm(productId: String): Boolean {
        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_NO_CREATE
        }
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = Constants.INTENT_ACTION_PRODUCT_NOTIFICATION
            putExtra(Constants.INTENT_PRODUCT_ID, productId)
        }

        val alarmIntent = PendingIntent.getBroadcast(
            context,
            productId.hashCode(),
            intent,
            flags
        )

        return alarmIntent != null
    }

    fun isExistRepeatAlarm(): Boolean {
        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_NO_CREATE
        }
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = Constants.INTENT_ACTION_NEW_DRAW_PRODUCT_NOTIFICATION
        }
        val alarmIntent = PendingIntent.getBroadcast(
            context,
            Constants.REQUEST_CODE_REPEAT_ALARM,
            intent,
            flags
        )

        return alarmIntent != null
    }

    // 상품 알림 설정
    fun setProductAlarm(
        triggerTime: Long,
        productId: String,
        productUrl: String
    ) {
        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
        // 브로드캐스트로 넘길 정보
        val alarmIntent = Intent(context, AlarmReceiver::class.java).apply {
            action = Constants.INTENT_ACTION_PRODUCT_NOTIFICATION
            putExtra(Constants.INTENT_PRODUCT_ID, productId)
        }
        val alarmPendingIntent = PendingIntent.getBroadcast(
            context,
            productId.hashCode(),
            alarmIntent,
            flags
        )

        // 알림에 대한 정보
        val alarmInfoIntent = Intent(
            Intent.ACTION_VIEW,
            (Constants.PRODUCT_DETAIL_URI + "/${productId}/${productUrl.substringAfter("t/")}")
                .toUri(),
            context,
            MainActivity::class.java
        )
        val alarmInfoPendingIntent = TaskStackBuilder.create(context).run {
            val actionFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }

            addNextIntentWithParentStack(alarmInfoIntent)
            getPendingIntent(0, actionFlags)
        }

        try {
            alarmManager.setAlarmClock(
                AlarmManager.AlarmClockInfo(
                    triggerTime,
                    alarmInfoPendingIntent
                ),
                alarmPendingIntent
            )
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    fun setRepeatNewDrawNotify() {
        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = Constants.INTENT_ACTION_NEW_DRAW_PRODUCT_NOTIFICATION
        }
        val alarmIntent = PendingIntent.getBroadcast(
            context,
            Constants.REQUEST_CODE_REPEAT_ALARM,
            intent,
            flags
        )
        val calendar: Calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, 12)
            set(Calendar.MINUTE, 0)

            if (this.timeInMillis < System.currentTimeMillis()) {
                set(Calendar.HOUR_OF_DAY, 24)
                set(Calendar.MINUTE, 0)
            }
        }

        try {
            alarmManager.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                AlarmManager.INTERVAL_HOUR * 6, // 6시간 씩 반복
                alarmIntent
            )
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    // 기존의 존재한 상품에 대한 알림을 해제 함
    fun cancelProductAlarm(productId: String) {
        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_NO_CREATE
        }
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = Constants.INTENT_ACTION_PRODUCT_NOTIFICATION
            putExtra(Constants.INTENT_PRODUCT_ID, productId)
        }

        val alarmIntent = PendingIntent.getBroadcast(
            context,
            productId.hashCode(),
            intent,
            flags
        )

        try {
            alarmManager.cancel(alarmIntent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun cancelRepeatNewDrawNotify() {
        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_NO_CREATE
        }
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = Constants.INTENT_ACTION_NEW_DRAW_PRODUCT_NOTIFICATION
        }
        val alarmIntent = PendingIntent.getBroadcast(
            context,
            Constants.REQUEST_CODE_REPEAT_ALARM,
            intent,
            flags
        )

        try {
            alarmManager.cancel(alarmIntent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun checkPermissions() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        areNotificationsEnabled() && canScheduleExactAlarms()
    } else {
        areNotificationsEnabled()
    }

    private fun areNotificationsEnabled() =
        NotificationManagerCompat.from(context).areNotificationsEnabled()

    @RequiresApi(Build.VERSION_CODES.S)
    private fun canScheduleExactAlarms() =
        alarmManager.canScheduleExactAlarms()
}