package com.nikealarm.core.domain.usecase

import com.nikealarm.core.common.Result
import com.nikealarm.core.domain.repository.DatabaseRepository
import com.nikealarm.core.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetSettingInitUseCase @Inject constructor(
    private val databaseRepository: DatabaseRepository,
    private val productRepository: ProductRepository,
    private val getAllowNotifyUseCase: GetAllowNotifyUseCase
) {
    data class NotificationPreferences(
        val isGeneralAlarmEnabled: Boolean,
        val isDrawAlarmEnabled: Boolean
    )

    operator fun invoke(): Flow<NotificationPreferences> {
        val drawNotifyFlow = databaseRepository.getAllowDrawNotification()
            .map { isAllowDraw ->
                val alarmResult = productRepository.isExistRepeatAlarm()
                val hasRepeatAlarm = (alarmResult is Result.ResultSuccess) && alarmResult.data

                isAllowDraw && hasRepeatAlarm
            }

        return combine(
            getAllowNotifyUseCase(),
            drawNotifyFlow
        ) { isGeneralEnabled, isDrawEnabled ->
            NotificationPreferences(isGeneralEnabled, isDrawEnabled)
        }
    }
}