package com.nikealarm.nikedrawalarm.component

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.util.Log
import androidx.work.*
import com.nikealarm.nikedrawalarm.component.worker.AutoEnterWorker
import com.nikealarm.nikedrawalarm.component.worker.FindDrawWorker
import com.nikealarm.nikedrawalarm.component.worker.ProductNotifyWorker
import com.nikealarm.nikedrawalarm.component.worker.ResetProductAlarmWorker
import com.nikealarm.nikedrawalarm.other.AlarmBuilder
import com.nikealarm.nikedrawalarm.other.Contents
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class MyAlarmReceiver : BroadcastReceiver() {
    @Inject
    @Named(Contents.PREFERENCE_NAME_TIME)
    lateinit var timePreferences: SharedPreferences

    @Inject
    @Named(Contents.PREFERENCE_NAME_AUTO_ENTER_V2)
    lateinit var autoEnterPerf: SharedPreferences

    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
        // 재부팅 및 앱 업데이트 후 알람 재설정
        if (intent.action == "android.intent.action.BOOT_COMPLETED" || intent.action == "android.intent.action.MY_PACKAGE_REPLACED") {
            reSetAlarm(context)
            reSetProductAlarm(context)
        } else {
            // 매일 데이터를 갱신 함
            if (intent.action == Contents.INTENT_ACTION_SYNC_ALARM) {
                reSetAlarm(context)

                if (isNotDawn()) { // 새벽이 아닐 때만 동작
                    val parsingWorkRequest = OneTimeWorkRequestBuilder<FindDrawWorker>()
                        .build()
                    WorkManager.getInstance(context).enqueue(parsingWorkRequest)
                }
            }
            // 특정 상품의 알림을 울림
            else if (intent.action == Contents.INTENT_ACTION_PRODUCT_ALARM) {
                // 상품의 대한 알림을 울림
                val dataPosition = intent.getStringExtra(Contents.INTENT_KEY_POSITION)
                val isDraw = intent.getBooleanExtra(Contents.INTENT_KEY_IS_DRAW, false)
                val isAllow = autoEnterPerf.getBoolean(Contents.AUTO_ENTER_ALLOW, false)
                Log.i("Check3", "동작")

                dataPosition?.let { shoesUrl ->
                    Log.i("Check4", "동작 $dataPosition")

                    if (isDraw && isAllow) { // Draw 상품이고 자동응모를 허용할 때
                        val workRequest: OneTimeWorkRequest =
                            OneTimeWorkRequestBuilder<AutoEnterWorker>()
                                .addTag(Contents.WORKER_AUTO_ENTER)
                                .setInputData(workDataOf(Contents.WORKER_AUTO_ENTER_INPUT_KEY to shoesUrl))
                                .build()

                        WorkManager.getInstance(context)
                            .enqueueUniqueWork(
                                Contents.WORKER_AUTO_ENTER,
                                ExistingWorkPolicy.APPEND_OR_REPLACE,
                                workRequest
                            )
                    } else { // Draw 상품이 아니거나 자동응모 허용하지 않을 때
//                        val workRequest = OneTimeWorkRequestBuilder<ProductNotifyWorker>()
//                            .setInputData(workDataOf(Contents.WORKER_INPUT_DATA_KEY to shoesUrl))
//                            .build()
//
//                        WorkManager.getInstance(context)
//                            .enqueue(workRequest)
                        val workRequest: OneTimeWorkRequest =
                            OneTimeWorkRequestBuilder<AutoEnterWorker>()
                                .addTag(Contents.WORKER_AUTO_ENTER)
                                .setInputData(workDataOf(Contents.WORKER_AUTO_ENTER_INPUT_KEY to shoesUrl))
                                .build()

                        WorkManager.getInstance(context)
                            .enqueueUniqueWork(
                                Contents.WORKER_AUTO_ENTER,
                                ExistingWorkPolicy.APPEND_OR_REPLACE,
                                workRequest
                            )
                    }
                }
            }
        }
    }

    // 시간 확인
    private fun isNotDawn(): Boolean {
        with(Calendar.getInstance()) {
            timeInMillis = System.currentTimeMillis()

            return this.get(Calendar.HOUR_OF_DAY) > 6
        }
    }

    // 알람 재설정
    private fun reSetAlarm(context: Context) {
        Log.i("Check", "동작")

        var timeTrigger = timePreferences.getLong(Contents.SYNC_ALARM_KEY, 0)

        if (timeTrigger != 0.toLong()) {
            Log.i("Check", "재설정")
            while (timeTrigger < System.currentTimeMillis()) {
                timeTrigger += 10800000
            }

            with(AlarmBuilder(context)) {
                setIntent(Contents.INTENT_ACTION_SYNC_ALARM)
                setAlarm(timeTrigger, Contents.SYNC_ALARM_CODE)
            }
            setPreference(timeTrigger)
        }
    }

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
