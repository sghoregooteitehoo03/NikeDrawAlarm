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
import com.nikealarm.nikedrawalarm.data.repository.ProductDatabaseRepository
import com.nikealarm.nikedrawalarm.data.repository.ProductRepository
import com.nikealarm.nikedrawalarm.domain.model.ProductCategory
import com.nikealarm.nikedrawalarm.domain.model.ProductInfo
import com.nikealarm.nikedrawalarm.domain.model.getProductFilter
import com.nikealarm.nikedrawalarm.domain.model.translateToProductInfoList
import com.nikealarm.nikedrawalarm.presentation.ui.MainActivity
import com.nikealarm.nikedrawalarm.util.Constants
import com.nikealarm.nikedrawalarm.util.NotificationBuilder
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.Locale

@HiltWorker
class DrawNotifyWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val productRepository: ProductRepository,
    private val databaseRepository: ProductDatabaseRepository
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val isAllow = databaseRepository.getAllowDrawNotification().first()
        if (!isAllow)
            return Result.failure()

        val productInfoList = mutableListOf<ProductInfo>()
        val retrofitService = productRepository.getRetrofitService()
        val data = retrofitService.getUpcomingProducts(0)

        data.objects.filter { // 제품들에 관해서만 필터링, Test 제품 걸러내기
            getProductFilter(it)
        }.forEach { filterProduct ->
            // 컬렉션 제품인 경우
            translateToProductInfoList(filterProduct).forEach { productInfo ->
                if (productInfo.eventDate != 0L) // 이미 출시 된 제품은 필터링 함
                    productInfoList.add(productInfo)
            }
        }

        // Draw 제품만 필터링
        val drawProductList = productInfoList.filter { it.category == ProductCategory.Draw }
        val notificationBuilder = NotificationBuilder(applicationContext)
        drawProductList.forEach {
            createNotification(it, notificationBuilder)
        }

        return Result.success()
    }

    private suspend fun createNotification(
        productInfo: ProductInfo,
        notificationBuilder: NotificationBuilder
    ) {
        val productId = productInfo.productId
        val productEntity = databaseRepository.getProductData(productId)

        if (productEntity == null) { // 사용자가 해당 Draw 제품을 보지 않았을 경우에만 동작
            val title = productInfo.title
            val contentText = SimpleDateFormat("M. d. a hh:mm 응모시작!", Locale.KOREA)
                .format(productInfo.eventDate)

            // Notification 클릭 시 시행할 동작
            val actionIntent = Intent(
                Intent.ACTION_VIEW,
                (Constants.PRODUCT_DETAIL_URI + "/${productId}/${
                    productInfo.url.substringAfter("t/")
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
                channelId = Constants.CHANNEL_ID_DRAW_NEW_PRODUCT_NOTIFICATION,
                notifyId = productId.hashCode(),
                title = title,
                contentText = contentText,
                image = productInfo.images[0],
                actionPendingIntent = actionPendingIntent
            )

            // 해당 제품은 사용자가 본 걸로 처리하기 위한 데이터 insert 동작
            databaseRepository.insertProductData(productInfo)
        }
    }
}