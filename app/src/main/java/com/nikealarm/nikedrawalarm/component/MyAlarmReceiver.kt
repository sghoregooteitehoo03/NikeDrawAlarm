package com.nikealarm.nikedrawalarm.component

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.util.Log
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.nikealarm.nikedrawalarm.component.worker.FindDrawWorker
import com.nikealarm.nikedrawalarm.component.worker.ProductNotifyWorker
import com.nikealarm.nikedrawalarm.component.worker.ResetProductAlarmWorker
import com.nikealarm.nikedrawalarm.database.Dao
import com.nikealarm.nikedrawalarm.database.MyDataBase
import com.nikealarm.nikedrawalarm.database.SpecialShoesDataModel
import com.nikealarm.nikedrawalarm.other.Contents
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class MyAlarmReceiver : BroadcastReceiver() {
    @Inject
    @Named(Contents.PREFERENCE_NAME_TIME)
    lateinit var timePreferences: SharedPreferences

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

                val parsingWorkRequest = OneTimeWorkRequestBuilder<FindDrawWorker>()
                    .build()
                WorkManager.getInstance(context).enqueue(parsingWorkRequest)
            }
            // 특정 상품의 알림을 울림
            else if (intent.action == Contents.INTENT_ACTION_PRODUCT_ALARM) {
                // 상품의 대한 알림을 울림
                val dataPosition = intent.getIntExtra(Contents.INTENT_KEY_POSITION, -1)
                Log.i("Check3", "동작")

                if (dataPosition != -1) {
                    Log.i("Check4", "동작 ${dataPosition}")
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

        var timeTrigger = timePreferences.getLong(Contents.SYNC_ALARM_KEY, 0)

        if (timeTrigger != 0.toLong()) {
            Log.i("Check", "재설정")
            while(timeTrigger < System.currentTimeMillis()) {
                timeTrigger += 10800000
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

            setPreference(timeTrigger)
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

    /* 테스트 해보기 */
    // 상품 알람 재설정
    private fun reSetProductAlarm(context: Context) {
        Log.i("Check2", "동작")
        val resetProductAlarmWorkRequest = OneTimeWorkRequestBuilder<ResetProductAlarmWorker>()
            .build()
        WorkManager.getInstance(context).enqueue(resetProductAlarmWorkRequest)
    }

    // 데이터베이스 설정
    private fun setPreference(timeTrigger: Long) {
        with(timePreferences.edit()) {
            putLong(Contents.SYNC_ALARM_KEY, timeTrigger)
            commit()
        }
    }
}
