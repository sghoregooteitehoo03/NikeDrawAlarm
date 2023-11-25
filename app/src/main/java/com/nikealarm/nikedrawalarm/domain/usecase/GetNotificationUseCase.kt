package com.nikealarm.nikedrawalarm.domain.usecase

import com.nikealarm.nikedrawalarm.data.model.entity.NotificationEntity
import com.nikealarm.nikedrawalarm.data.repository.ProductDatabaseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetNotificationUseCase @Inject constructor(
    private val repository: ProductDatabaseRepository,
//    private val getAllowNotifyUseCase: GetAllowNotifyUseCase
) {
    operator fun invoke(productId: String) = channelFlow {
//        getAllowNotifyUseCase().first()
        repository.getNotificationData(productId)
            .collectLatest { entity ->
                send(entity)
            }

        close()
    }
}