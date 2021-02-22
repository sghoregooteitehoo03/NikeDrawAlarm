package com.nikealarm.nikedrawalarm.component.worker

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.nikealarm.nikedrawalarm.database.Dao
import com.nikealarm.nikedrawalarm.database.SpecialShoesDataModel
import com.nikealarm.nikedrawalarm.other.Contents
import com.nikealarm.nikedrawalarm.other.NotificationBuilder
import com.nikealarm.nikedrawalarm.ui.MainActivity
import com.squareup.picasso.Picasso
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import javax.inject.Named
import kotlin.random.Random

@HiltWorker
class ProductNotifyWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    @Named(Contents.PREFERENCE_NAME_TIME) val timePreferences: SharedPreferences,
    @Named(Contents.PREFERENCE_NAME_ALLOW_ALARM) val allowAlarmPreferences: SharedPreferences,
    private val mDao: Dao
) : Worker(
    appContext,
    workerParams
) {

    override fun doWork(): Result {
        val shoesUrl = inputData.getString(Contents.WORKER_INPUT_DATA_KEY)
        val index = mDao.getAllSpecialShoesData()
            .indexOf(SpecialShoesDataModel(0, "", "", null, null, shoesUrl))

        Log.i("Check5", "position: $index")
        if (index != -1) {
            val drawData = mDao.getAllSpecialShoesData()[index]
            val random = Random(System.currentTimeMillis())

            createNotification(drawData, random.nextInt(100) + index)

            // 알림 후 해당 상품을 db에서 지움
            deleteShoesData(drawData)
        }

        return Result.success()
    }

    private fun createNotification(
        shoesData: SpecialShoesDataModel,
        channelId: Int
    ) {
        val goEventPendingIntent = PendingIntent.getActivity(
            applicationContext,
            channelId,
            Intent(applicationContext, MainActivity::class.java).also {
                it.action = Contents.INTENT_ACTION_GOTO_WEBSITE
                it.putExtra(Contents.DRAW_URL, shoesData.ShoesUrl)
                it.putExtra(Contents.CHANNEL_ID, channelId)
            },
            PendingIntent.FLAG_ONE_SHOT
        )
        val bitmap = Picasso.get().load(shoesData.ShoesImageUrl).get()

        with(NotificationBuilder(applicationContext, Contents.CHANNEL_ID_SHOES, "상품 알림")) {
            imageNotification(
                "해당 상품이 출시되었습니다.",
                "${shoesData.ShoesSubTitle} - ${shoesData.ShoesTitle}",
                bitmap,
                true
            )
            addActions(arrayOf("바로가기"), arrayOf(goEventPendingIntent))

            buildNotify(channelId)
        }
    }

    // 데이터를 지움
    private fun deleteShoesData(data: SpecialShoesDataModel) {
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