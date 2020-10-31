package com.nikealarm.nikedrawalarm.component

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.nikealarm.nikedrawalarm.R
import com.nikealarm.nikedrawalarm.database.Dao
import com.nikealarm.nikedrawalarm.database.MyDataBase
import com.nikealarm.nikedrawalarm.database.SpecialShoesDataModel
import com.nikealarm.nikedrawalarm.other.Contents
import com.nikealarm.nikedrawalarm.ui.MainActivity
import com.squareup.picasso.Picasso

class ProductNotifyWorker(context: Context, workerParams: WorkerParameters) : Worker(
    context,
    workerParams
) {
    private lateinit var mDao: Dao

    override fun doWork(): Result {
        mDao = MyDataBase.getDatabase(applicationContext)!!.getDao()
        val position = inputData.getInt(Contents.WORKER_INPUT_DATA_KEY, -1)

        Log.i("Check5", "position: ${position}")
        if (position != -1) {
            val drawData = mDao.getAllSpecialShoesData()[position]

            createNotification(drawData, applicationContext)

            // 알림 후 해당 상품을 db에서 지움
            deleteShoesData(drawData)
        }

        return Result.success()
    }

    private fun createNotification(specialInfo: SpecialShoesDataModel, context: Context) {
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
                it.putExtra(Contents.DRAW_URL, specialInfo.ShoesUrl)
            },
            PendingIntent.FLAG_ONE_SHOT
        )
        val bitmap = Picasso.get().load(Uri.parse(specialInfo.ShoesImageUrl)).get()
        val notificationBuilder = NotificationCompat.Builder(context, "Default")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("${specialInfo.ShoesSubTitle} - ${specialInfo.ShoesTitle}")
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "Default",
                specialInfo.ShoesTitle,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            notificationManager.createNotificationChannel(channel)
        }


        with(NotificationManagerCompat.from(context)) {
            notify(100, notificationBuilder.build())
        }
    }

    // 데이터를 지움
    private fun deleteShoesData(data: SpecialShoesDataModel) {
        val timeSharedPreference = applicationContext.getSharedPreferences(
            Contents.PREFERENCE_NAME_TIME,
            Context.MODE_PRIVATE
        )
        val allowAlarmPreference = applicationContext.getSharedPreferences(
            Contents.PREFERENCE_NAME_ALLOW_ALARM,
            Context.MODE_PRIVATE
        )

        with(timeSharedPreference.edit()) {
            remove("${data.ShoesTitle}-${data.ShoesSubTitle}")
            commit()
        }

        mDao.deleteSpecialData(data.ShoesUrl!!)
        with(allowAlarmPreference.edit()) {
            remove("${data.ShoesTitle}-${data.ShoesSubTitle}")
            commit()
        }

    }
}