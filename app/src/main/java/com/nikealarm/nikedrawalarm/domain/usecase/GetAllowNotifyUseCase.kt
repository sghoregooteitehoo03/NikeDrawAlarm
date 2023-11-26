package com.nikealarm.nikedrawalarm.domain.usecase

import com.nikealarm.nikedrawalarm.data.repository.ProductDatabaseRepository
import com.nikealarm.nikedrawalarm.data.repository.ProductRepository
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

class GetAllowNotifyUseCase @Inject constructor(
    private val productRepository: ProductRepository,
    private val databaseRepository: ProductDatabaseRepository,
    private val allowNotificationUseCase: AllowNotificationUseCase
) {
    operator fun invoke() = channelFlow {
        databaseRepository.getAllowNotification()
            .collectLatest { isAllow ->
                if (productRepository.checkAlarmPermissions()) { // 권한이 설정되어 있는 경우
                    send(isAllow)
                } else { // 권한이 해제되어 있는 경우
                    if (isAllow) { // 기존에 허용한 알람들을 다 해지함
                        allowNotificationUseCase(false)
                    }

                    send(false)
                }
            }

        close()
    }
}