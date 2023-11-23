package com.nikealarm.nikedrawalarm.Component.work

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.TaskStackBuilder
import androidx.core.net.toUri
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.nikealarm.nikedrawalarm.R
import com.nikealarm.nikedrawalarm.data.model.entity.NotificationEntity
import com.nikealarm.nikedrawalarm.data.model.entity.ProductEntity
import com.nikealarm.nikedrawalarm.data.repository.ProductDatabaseRepository
import com.nikealarm.nikedrawalarm.presentation.ui.MainActivity
import com.nikealarm.nikedrawalarm.util.Constants
import com.squareup.picasso.Picasso
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.Locale

@HiltWorker
class ProductNotificationWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val databaseRepository: ProductDatabaseRepository
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val productId = inputData.getString(Constants.INTENT_PRODUCT_ID) ?: return Result.failure()
        val productEntity = databaseRepository.getProductData(productId) ?: return Result.failure()
        val notificationEntity =
            databaseRepository.getNotificationData(productId).first() ?: return Result.failure()

        setNotification(productEntity, notificationEntity) // 알림 생성
        databaseRepository.deleteNotificationData(productId) // 알림 생성 끝나면 데이터베이스에서 지움
        return Result.success()
    }

    private fun setNotification(
        productEntity: ProductEntity,
        notificationEntity: NotificationEntity
    ) {
        val title = productEntity.title
        val productCategory = when (productEntity.category) {
            "Coming" -> "출시"
            "Draw" -> "응모"
            else -> ""
        }
        val contentText = if (notificationEntity.notificationDate >= 3600000L) {
            SimpleDateFormat(
                "제품 ${productCategory} h시간 전 입니다.",
                Locale.KOREA
            ).format(notificationEntity.notificationDate.minus(32400000L))
        } else {
            SimpleDateFormat(
                "제품 ${productCategory} m분 전 입니다.",
                Locale.KOREA
            ).format(notificationEntity.notificationDate)
        }
        val image = Picasso.get().load(productEntity.thumbnailImage).get()

        // Notification 클릭 시 시행할 동작
        val actionIntent = Intent(
            Intent.ACTION_VIEW,
            (Constants.PRODUCT_DETAIL_URI + "/${productEntity.productId}/${
                productEntity.url.substringAfter("t/")
            }").toUri(),
            applicationContext,
            MainActivity::class.java
        )
        val actionPendingIntent = TaskStackBuilder.create(applicationContext).run {
            val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }

            addNextIntentWithParentStack(actionIntent)
            getPendingIntent(0, flags)
        }

        val builder = NotificationCompat.Builder(
            applicationContext,
            Constants.CHANNEL_ID_PRODUCT_NOTIFICATION
        )
            .setSmallIcon(R.mipmap.ic_launcher)
            .setLargeIcon(image)
            .setVibrate(longArrayOf(0, 100, 200, 300))
            .setContentTitle(title)
            .setContentText(contentText)
            .setContentIntent(actionPendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        if (ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            NotificationManagerCompat.from(applicationContext)
                .notify(productEntity.productId.hashCode(), builder)
        }
    }
}