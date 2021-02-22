package com.nikealarm.nikedrawalarm.component.worker

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.nikealarm.nikedrawalarm.database.Dao
import com.nikealarm.nikedrawalarm.database.ShoesDataModel
import com.nikealarm.nikedrawalarm.database.SpecialShoesDataModel
import com.nikealarm.nikedrawalarm.other.AlarmBuilder
import com.nikealarm.nikedrawalarm.other.Contents
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import javax.inject.Named

@HiltWorker
class ResetProductAlarmWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    @Named(Contents.PREFERENCE_NAME_TIME) val timePreferences: SharedPreferences,
    @Named(Contents.PREFERENCE_NAME_ALLOW_ALARM) val allowAlarmPreferences: SharedPreferences,
    private val mDao: Dao
) : Worker(appContext, workerParams) {

    override fun doWork(): Result {
        resetProductAlarm()
        return Result.success()
    }

    private fun resetProductAlarm() {
        for (position in mDao.getAllSpecialShoesData().indices) {
            val shoesData = mDao.getAllSpecialShoesData()[position]
            val preferenceKey = shoesData.ShoesUrl
            val timeTrigger = timePreferences.getLong(preferenceKey, 0)

            if (timeTrigger != 0L) {
                Log.i("CheckTime", "$timeTrigger")
                if (timeTrigger < System.currentTimeMillis()) {
                    deleteDrawShoesData(shoesData)
                    continue
                }

                with(AlarmBuilder(applicationContext)) {
                    val bundle = Bundle().apply {
                        putString(Contents.INTENT_KEY_POSITION, shoesData.ShoesUrl)

                        if (shoesData.ShoesCategory == ShoesDataModel.CATEGORY_DRAW) {
                            putBoolean(Contents.INTENT_KEY_IS_DRAW, true)
                        }
                    }
                    setIntent(Contents.INTENT_ACTION_PRODUCT_ALARM, bundle)
                    setAlarm(timeTrigger, shoesData.ShoesId!!)
                }
            }
        }
    }

    private fun deleteDrawShoesData(data: SpecialShoesDataModel) {
        with(timePreferences.edit()) {
            remove(data.ShoesUrl)
            commit()
        }

        with(allowAlarmPreferences.edit()) {
            remove(data.ShoesUrl)
            commit()
        }

        mDao.deleteSpecialData(data.ShoesUrl!!)
    }
}