package com.nikealarm.core.domain.usecase

import com.nikealarm.core.common.Result
import com.nikealarm.core.domain.repository.DatabaseRepository
import com.nikealarm.core.domain.repository.ProductRepository
import com.nikealarm.core.model.Notification
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetNotificationUseCase @Inject constructor(
    private val databaseRepository: DatabaseRepository,
    private val productRepository: ProductRepository
) {
    operator fun invoke(productId: String, eventDate: Long) =
        databaseRepository.getNotificationData(productId)
            .map { notification ->
                val isEventActive = eventDate != 0L

                val alarmResult = productRepository.isExistProductAlarm(productId)
                val hasAlarm = (alarmResult is Result.ResultSuccess) && alarmResult.data

                val isValid = isEventActive && hasAlarm && notification != null
                if (isValid) {
                    return@map notification
                }

                // TODO: 부수효과 제거
                if (notification != null) { // 쓸데없이 남아있는 쓰레기 데이터 청소
                    databaseRepository.deleteNotificationData(productId)
                }

                if (isEventActive) {
                    // 이벤트는 진행 중이지만 알람이 꺼졌거나 데이터가 없는 경우
                    return@map Notification(productId, 0L, 0L, 0L)
                } else {
                    // 이벤트가 아예 끝난 경우
                    return@map null
                }
            }
}