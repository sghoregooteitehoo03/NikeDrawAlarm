package com.nikealarm.nikedrawalarm.util

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.nikealarm.nikedrawalarm.R
import com.squareup.picasso.Picasso

class NotificationBuilder(
    private val context: Context
) {
    fun createNotification(
        channelId: String,
        notifyId: Int,
        title: String,
        contentText: String,
        image: String,
        actionPendingIntent: PendingIntent?
    ) {
        val imageBitmap = Picasso.get().load(image).get()
        val builder = NotificationCompat.Builder(
            context,
            channelId
        )
            .setSmallIcon(R.drawable.app_logo_v2)
            .setLargeIcon(imageBitmap)
            .setVibrate(longArrayOf(0, 100, 200, 300))
            .setContentTitle(title)
            .setContentText(contentText)
            .setContentIntent(actionPendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            NotificationManagerCompat.from(context)
                .notify(notifyId, builder)
        }
    }
}