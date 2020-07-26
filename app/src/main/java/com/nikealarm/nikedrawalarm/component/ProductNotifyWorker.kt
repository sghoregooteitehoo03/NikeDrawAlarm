package com.nikealarm.nikedrawalarm.component

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.nikealarm.nikedrawalarm.R
import com.nikealarm.nikedrawalarm.database.Dao
import com.nikealarm.nikedrawalarm.database.DrawShoesDataModel
import com.nikealarm.nikedrawalarm.database.MyDataBase
import com.nikealarm.nikedrawalarm.other.Contents
import com.nikealarm.nikedrawalarm.ui.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProductNotifyWorker(context: Context, workerParams: WorkerParameters) : Worker(context,
    workerParams
) {
    private lateinit var mDao: Dao

    override fun doWork(): Result {
        mDao = MyDataBase.getDatabase(applicationContext)!!.getDao()
        val position = inputData.getInt(Contents.WORKER_INPUT_DATA_KEY, -1)

        if(position != -1) {
            val data = mDao.getAllDrawShoesData()[position]

            createNotification(data, applicationContext)

            // 알림 후 해당 상품을 db에서 지움
            deleteShoesData(data)
        }

        return Result.success()
    }

    private fun createNotification(drawShoesInfo: DrawShoesDataModel, context: Context) {
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
            .setSmallIcon(R.mipmap.ic_launcher)
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


        with(NotificationManagerCompat.from(context)) {
            notify(100, notificationBuilder.build())
        }
    }

    // 데이터를 지움
    private fun deleteShoesData(data: DrawShoesDataModel) {
        val mSharedPreferences = applicationContext.getSharedPreferences(Contents.PREFERENCE_NAME_TIME, Context.MODE_PRIVATE)
        with(mSharedPreferences.edit()) {
            remove(data.shoesTitle)
            commit()
        }

        CoroutineScope(Dispatchers.IO).launch {
            mDao.deleteDrawShoesData(data)
        }
    }
}