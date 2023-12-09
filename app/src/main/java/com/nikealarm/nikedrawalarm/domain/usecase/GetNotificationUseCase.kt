package com.nikealarm.nikedrawalarm.domain.usecase

import com.nikealarm.nikedrawalarm.data.model.entity.NotificationEntity
import com.nikealarm.nikedrawalarm.data.repository.ProductDatabaseRepository
import com.nikealarm.nikedrawalarm.data.repository.ProductRepository
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetNotificationUseCase @Inject constructor(
    private val databaseRepository: ProductDatabaseRepository,
    private val productRepository: ProductRepository
) {
    operator fun invoke(productId: String, eventDate: Long) =
        databaseRepository.getNotificationData(productId)
            .map { notificationEntity ->
                if (eventDate != 0L) { // 이벤트 중인 상품일 때
                    if (productRepository.isExistProductAlarm(productId) && notificationEntity != null) {
                        notificationEntity // 알람 설정이 되어 있으면 알림 데이터가 존재할 때
                    } else {
                        if (notificationEntity != null)
                            databaseRepository.deleteNotificationData(productId)

                        NotificationEntity(productId, 0L, 0L, 0L)
                    }
                } else { // 이벤트 중인 상품이 아닐 때
                    if (notificationEntity != null) // 알림 데이터가 남아 있는 경우
                        databaseRepository.deleteNotificationData(productId)

                    null
                }
            }
}