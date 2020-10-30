package com.nikealarm.nikedrawalarm.component.worker

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.util.Log
import androidx.hilt.Assisted
import androidx.hilt.work.WorkerInject
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.nikealarm.nikedrawalarm.component.MyAlarmReceiver
import com.nikealarm.nikedrawalarm.database.Dao
import com.nikealarm.nikedrawalarm.database.MyDataBase
import com.nikealarm.nikedrawalarm.database.SpecialShoesDataModel
import com.nikealarm.nikedrawalarm.other.Contents
import javax.inject.Named

class ResetProductAlarmWorker @WorkerInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    @Named(Contents.PREFERENCE_NAME_TIME) val timePreferences: SharedPreferences,
    @Named(Contents.PREFERENCE_NAME_ALLOW_ALARM) val allowAlarmPreferences: SharedPreferences
) : Worker(appContext, workerParams) {
    private lateinit var mDao: Dao

    override fun doWork(): Result {
        mDao = MyDataBase.getDatabase(applicationContext)!!.getDao()

        resetProductAlarm()
        return Result.success()
    }

    private fun resetProductAlarm() {
        for (position in mDao.getAllSpecialShoesData().indices) {
            val shoesData = mDao.getAllSpecialShoesData()[position]
            val preferenceKey = "${shoesData.ShoesTitle}-${shoesData.ShoesSubTitle}"
            val timeTrigger = timePreferences.getLong(preferenceKey, 0)

            if (timeTrigger != 0L) {
                Log.i("CheckTime", "${timeTrigger}")
                if (timeTrigger < System.currentTimeMillis()) {
                    deleteDrawShoesData(shoesData)
                    continue
                }

                val mAlarmManager =
                    applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager

                val reIntent = Intent(applicationContext, MyAlarmReceiver::class.java).apply {
                    action = Contents.INTENT_ACTION_PRODUCT_ALARM
                    putExtra(Contents.INTENT_KEY_POSITION, position)
                }

                val alarmPendingIntent = PendingIntent.getBroadcast(
                    applicationContext,
                    position,
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

    private fun deleteDrawShoesData(data: SpecialShoesDataModel) {
        with(timePreferences.edit()) {
            remove("${data.ShoesTitle}-${data.ShoesSubTitle}")
            commit()
        }

        with(allowAlarmPreferences.edit()) {
            remove("${data.ShoesTitle}-${data.ShoesSubTitle}")
            commit()
        }

        mDao.deleteSpecialData(data.ShoesUrl!!)
    }
}