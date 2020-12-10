package com.nikealarm.nikedrawalarm.component.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.Assisted
import androidx.hilt.work.WorkerInject
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.nikealarm.nikedrawalarm.R
import com.nikealarm.nikedrawalarm.database.Dao
import com.nikealarm.nikedrawalarm.database.ShoesDataModel
import com.nikealarm.nikedrawalarm.database.SpecialShoesDataModel
import com.nikealarm.nikedrawalarm.other.Contents
import com.nikealarm.nikedrawalarm.ui.MainActivity
import com.squareup.picasso.Picasso
import javax.inject.Named
import kotlin.random.Random

class ProductNotifyWorker @WorkerInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    @Named(Contents.PREFERENCE_NAME_TIME) val timePreferences: SharedPreferences,
    @Named(Contents.PREFERENCE_NAME_ALLOW_ALARM) val allowAlarmPreferences: SharedPreferences,
    @Named(Contents.PREFERENCE_NAME_AUTO_ENTER_V2) val autoEnterPreferences: SharedPreferences,
    val mDao: Dao
) : Worker(
    appContext,
    workerParams
) {

    override fun doWork(): Result {
        val shoesUrl = inputData.getString(Contents.WORKER_INPUT_DATA_KEY)
        val index = mDao.getAllSpecialShoesData().indexOf(SpecialShoesDataModel(0, "", "", null, null, shoesUrl))

        Log.i("Check5", "position: $index")
        if (index != -1) {
            val drawData = mDao.getAllSpecialShoesData()[index]
            val random = Random(System.currentTimeMillis())

            createNotification(drawData, applicationContext, random.nextInt(100) + index)

            // 알림 후 해당 상품을 db에서 지움
            deleteShoesData(drawData)
        }

        return Result.success()
    }

    private fun createNotification(
        shoesData: SpecialShoesDataModel,
        context: Context,
        channelId: Int
    ) {
        val vibrate = LongArray(4).apply {
            set(0, 0)
            set(1, 100)
            set(2, 200)
            set(3, 300)
        }

        val goEventPendingIntent = PendingIntent.getActivity(
            context,
            channelId,
            Intent(context, MainActivity::class.java).also {
                it.action = Contents.INTENT_ACTION_GOTO_WEBSITE
                it.putExtra(Contents.DRAW_URL, shoesData.ShoesUrl)
                it.putExtra(Contents.CHANNEL_ID, channelId)
            },
            PendingIntent.FLAG_ONE_SHOT
        )
        val autoEnterPendingIntent = PendingIntent.getActivity(
            context,
            channelId,
            Intent(context, MainActivity::class.java).also {
                it.action = Contents.INTENT_ACTION_GOTO_AUTO_ENTER
                it.putExtra(Contents.DRAW_URL, shoesData.ShoesUrl)
                it.putExtra(Contents.CHANNEL_ID, channelId)
            },
            PendingIntent.FLAG_ONE_SHOT
        )
        val bitmap = Picasso.get().load(shoesData.ShoesImageUrl).get()
        val notificationBuilder = NotificationCompat.Builder(context, "Default")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("${shoesData.ShoesSubTitle} - ${shoesData.ShoesTitle}")
            .setVibrate(vibrate)
            .setLargeIcon(bitmap)
            .setStyle(NotificationCompat.BigTextStyle())
            .setStyle(
                NotificationCompat.BigPictureStyle()
                    .bigPicture(bitmap)
                    .bigLargeIcon(null)
            )
            .setContentText("해당 상품이 출시되었습니다.")
            .addAction(0, "바로가기", goEventPendingIntent)

        // DRAW 상품 자동응모 허용할 시 자동응모 버튼 추가
        if (shoesData.ShoesCategory == ShoesDataModel.CATEGORY_DRAW
            && autoEnterPreferences.getBoolean(
                Contents.AUTO_ENTER_ALLOW,
                false
            )
        ) {
            notificationBuilder.addAction(0, "자동응모 하기", autoEnterPendingIntent)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "Default",
                shoesData.ShoesTitle,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            notificationManager.createNotificationChannel(channel)
        }


        with(NotificationManagerCompat.from(context)) {
            notify(channelId, notificationBuilder.build())
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