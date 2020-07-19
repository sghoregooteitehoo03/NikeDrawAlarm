package com.nikealarm.nikedrawalarm

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class MyAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.

        // 매일 데이터를 갱신 함
        if (intent.action == Contents.INTENT_ACTION_SYNC_ALARM) {
            reSetAlarm(context, intent.getLongExtra(Contents.SET_ALARM, 0))

            val parsingWorkRequest = OneTimeWorkRequestBuilder<ParsingWorker>()
                .build()
            WorkManager.getInstance(context).enqueue(parsingWorkRequest)
        }
        // 특정 상품의 알림을 울림
        else if (intent.action == Contents.INTENT_ACTION_PRODUCT_ALARM) {
            // 상품의 대한 알림을 울림
            val dataPosition = intent.getIntExtra(Contents.INTENT_KEY_POSITION, -1)
            val mDao = MyDataBase.getDatabase(context)!!.getDao()

            if (dataPosition != -1) {
                CoroutineScope(Dispatchers.IO).launch {
                    createNotification(mDao.getAllShoesData()[dataPosition], context)
                }
            }
        }
    }

    // 알람 재설정
    private fun reSetAlarm(mContext: Context, timeTrigger: Long) {
        if (timeTrigger != 0.toLong()) {
            val mAlarmManager = mContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            val reIntent = Intent(mContext, MyAlarmReceiver::class.java).apply {
                putExtra(Contents.SET_ALARM, timeTrigger + 86400000)
            }

            val alarmPendingIntent = PendingIntent.getBroadcast(
                mContext,
                Contents.SYNC_ALARM_CODE,
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

    private suspend fun createNotification(drawShoesInfo: DrawShoesDataModel, context: Context) {
        val vibrate = LongArray(4).apply {
            set(0, 0)
            set(1, 100)
            set(2, 200)
            set(3, 300)
        }

        val goEventPendingIntent = PendingIntent.getActivity(
            context,
            5000,
            Intent(context, MainActivity::class.java).also {
                it.action = Contents.INTENT_ACTION_GOTO_WEBSITE
                it.putExtra(Contents.DRAW_URL, drawShoesInfo.url)
            },
            PendingIntent.FLAG_ONE_SHOT
        )

        val notificationBuilder = NotificationCompat.Builder(context, "Default")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("${drawShoesInfo.shoesSubTitle} - ${drawShoesInfo.shoesTitle}")
            .setVibrate(vibrate)
            .setLargeIcon(drawShoesInfo.shoesImage)
            .setStyle(NotificationCompat.BigTextStyle())
            .setStyle(
                NotificationCompat.BigPictureStyle()
                    .bigPicture(drawShoesInfo.shoesImage)
                    .bigLargeIcon(null)
            )
            .setContentText("해당 상품의 Draw 이벤트가 시작되었습니다.")
            .addAction(0, "응모하러 가기", goEventPendingIntent)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "Default",
                drawShoesInfo.shoesTitle,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            notificationManager.createNotificationChannel(channel)
        }

        withContext(Dispatchers.Main) {
            with(NotificationManagerCompat.from(context)) {
                notify(100, notificationBuilder.build())
            }
        }
    }
}
