package com.nikealarm.nikedrawalarm.component

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.util.Log
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.nikealarm.nikedrawalarm.database.MyDataBase
import com.nikealarm.nikedrawalarm.other.Contents
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MyAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
        // 재부팅 시 알람 재설정
        if (intent.action == "android.intent.action.BOOT_COMPLETED") {
            reSetAlarm(context)
            reSetProductAlarm(context)
        } else {
            // 매일 데이터를 갱신 함
            if (intent.action == Contents.INTENT_ACTION_SYNC_ALARM) {
                reSetAlarm(context)

                val parsingWorkRequest = OneTimeWorkRequestBuilder<ParsingWorker>()
                    .build()
                WorkManager.getInstance(context).enqueue(parsingWorkRequest)
            }
            // 특정 상품의 알림을 울림
            else if (intent.action == Contents.INTENT_ACTION_PRODUCT_ALARM) {
                // 상품의 대한 알림을 울림
                val dataPosition = intent.getIntExtra(Contents.INTENT_KEY_POSITION, -1)
                Log.i("Check3", "동작")

                if (dataPosition != -1) {
                    val productNotifyWorkRequest = OneTimeWorkRequestBuilder<ProductNotifyWorker>()
                        .setInputData(workDataOf(Contents.WORKER_INPUT_DATA_KEY to dataPosition))
                        .build()
                    WorkManager.getInstance(context).enqueue(productNotifyWorkRequest)
                }
            }
        }
    }

    // 알람 재설정
    private fun reSetAlarm(context: Context) {
        Log.i("Check", "동작")

        val mSharedPreferences =
            context.getSharedPreferences(Contents.PREFERENCE_NAME_TIME, Context.MODE_PRIVATE)
        var timeTrigger = mSharedPreferences.getLong(Contents.SYNC_ALARM_KEY, 0)

        if (timeTrigger != 0.toLong()) {
            if (timeTrigger < System.currentTimeMillis()) {
                timeTrigger += 86400000
            }

            val mAlarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            val reIntent = Intent(context, MyAlarmReceiver::class.java).apply {
                action = Contents.INTENT_ACTION_SYNC_ALARM
            }

            val alarmPendingIntent = PendingIntent.getBroadcast(
                context,
                Contents.SYNC_ALARM_CODE,
                reIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )

            setPreference(mSharedPreferences, timeTrigger, Contents.SYNC_ALARM_KEY)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mAlarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    timeTrigger,
                    alarmPendingIntent
                )
            } else {
                mAlarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    timeTrigger,
                    alarmPendingIntent
                )
            }
        }
    }

    private fun reSetProductAlarm(context: Context) {
        Log.i("Check2", "동작")
        val mSharedPreferences =
            context.getSharedPreferences(Contents.PREFERENCE_NAME_TIME, Context.MODE_PRIVATE)

        CoroutineScope(Dispatchers.IO).launch {
            val mDao = MyDataBase.getDatabase(context)!!.getDao()

            for (shoesData in mDao.getAllShoesData()) {
                val preferenceKey = shoesData.shoesTitle
                val timeTrigger = mSharedPreferences.getLong(preferenceKey, 0)

                if (timeTrigger != 0L) {
                    Log.i("CheckTime", "${timeTrigger}")
                    val index = mDao.getAllShoesData().indexOf(shoesData)

                    if (timeTrigger < System.currentTimeMillis()) {
                        mDao.deleteShoesData(shoesData)
                        return@launch
                    }

                    val mAlarmManager =
                        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

                    val reIntent = Intent(context, MyAlarmReceiver::class.java).apply {
                        action = Contents.INTENT_ACTION_PRODUCT_ALARM
                        putExtra(Contents.INTENT_KEY_POSITION, index)
                    }

                    val alarmPendingIntent = PendingIntent.getBroadcast(
                        context,
                        index,
                        reIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                    )

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        mAlarmManager.setExactAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP,
                            timeTrigger,
                            alarmPendingIntent
                        )
                    } else {
                        mAlarmManager.setExact(
                            AlarmManager.RTC_WAKEUP,
                            timeTrigger,
                            alarmPendingIntent
                        )
                    }
                }
            }
        }
    }

    private fun setPreference(preference: SharedPreferences, timeTrigger: Long, useKey: String) {
        with(preference.edit()) {
            putLong(useKey, timeTrigger)
            commit()
        }
    }
}
