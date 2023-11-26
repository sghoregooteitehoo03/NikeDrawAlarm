package com.nikealarm.nikedrawalarm.Component.work

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.TaskStackBuilder
import androidx.core.net.toUri
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.nikealarm.nikedrawalarm.data.model.entity.NotificationEntity
import com.nikealarm.nikedrawalarm.data.model.entity.ProductEntity
import com.nikealarm.nikedrawalarm.data.repository.ProductDatabaseRepository
import com.nikealarm.nikedrawalarm.presentation.ui.MainActivity
import com.nikealarm.nikedrawalarm.util.Constants
import com.nikealarm.nikedrawalarm.util.NotificationBuilder
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
        val isAllowNotify = databaseRepository.getAllowNotification().first()

        return if (isAllowNotify) { // 설정에서 알림허용을 하였을 경우에만 동작
            val productId =
                inputData.getString(Constants.INTENT_PRODUCT_ID) ?: return Result.failure()
            // TODO: 알림 안울리는 버그 발생
            val productEntity =
                databaseRepository.getProductData(productId) ?: return Result.failure()
            val notificationEntity =
                databaseRepository.getNotificationData(productId).first() ?: return Result.failure()

            createNotification(productEntity, notificationEntity) // 알림 생성
            databaseRepository.deleteNotificationData(productId) // 알림 생성 끝나면 데이터베이스에서 지움
            Result.success()
        } else {
            Result.failure()
        }
    }

    private fun createNotification(
        productEntity: ProductEntity,
        notificationEntity: NotificationEntity
    ) {
        val notificationBuilder = NotificationBuilder(applicationContext)
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

        notificationBuilder.createNotification(
            channelId = Constants.CHANNEL_ID_PRODUCT_NOTIFICATION,
            notifyId = productEntity.productId.hashCode(),
            title = title,
            contentText = contentText,
            image = productEntity.thumbnailImage,
            actionPendingIntent = actionPendingIntent
        )
    }
}