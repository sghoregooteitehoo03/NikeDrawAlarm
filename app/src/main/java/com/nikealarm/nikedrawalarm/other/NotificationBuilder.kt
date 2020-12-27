package com.nikealarm.nikedrawalarm.other

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.nikealarm.nikedrawalarm.R

class NotificationBuilder(
    private val context: Context,
    private val channelId: String,
    private val channelName: String
) {
    private val notification = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(R.mipmap.ic_launcher)
        .setVibrate(longArrayOf(0, 100, 200, 300))

    fun defaultNotification(title: String, message: String) {
        notification.apply {
            setContentTitle(title)
            setContentText(message)
        }
    }

    fun imageNotification(
        title: String,
        message: String,
        image: Bitmap,
        isBigImage: Boolean = false
    ) {
        notification.apply {
            setContentTitle(title)
            setContentText(message)
            setLargeIcon(image)

            if (isBigImage) {
                setStyle(NotificationCompat.BigTextStyle())
                setStyle(
                    NotificationCompat.BigPictureStyle()
                        .bigPicture(image)
                        .bigLargeIcon(null)
                )
            }
        }
    }

    fun addActions(titles: Array<String>, actions: Array<PendingIntent>) {
        for(i in titles.indices) {
            notification.addAction(0, titles[i], actions[i])
        }
    }

    fun buildNotify(id: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel()
        }

        with(NotificationManagerCompat.from(context)) {
            notify(id, notification.build())
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannel() {
        val channel = NotificationChannel(
            channelId,
            channelName,
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.createNotificationChannel(channel)
    }
}