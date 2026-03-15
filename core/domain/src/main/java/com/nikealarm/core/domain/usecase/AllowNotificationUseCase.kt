package com.nikealarm.core.domain.usecase

import com.nikealarm.core.common.Result
import com.nikealarm.core.common.getOrThrow
import com.nikealarm.core.common.runCatching
import com.nikealarm.core.domain.repository.DatabaseRepository
import com.nikealarm.core.model.ClearProductType
import javax.inject.Inject

class AllowNotificationUseCase @Inject constructor(
    private val databaseRepository: DatabaseRepository,
    private val clearProductUseCase: ClearProductUseCase,
    private val allowDrawNotifyUseCase: AllowDrawNotifyUseCase
) {

    suspend operator fun invoke(isAllow: Boolean): Result<Unit> {
        return runCatching {
            if (!isAllow) { // 알림을 해제하는 경우 기존에 설정 된 알림들을 지움
                clearProductUseCase(ClearProductType.ClearNotifyProduct).getOrThrow()
                allowDrawNotifyUseCase(false).getOrThrow()
            }

            databaseRepository.setAllowNotification(isAllow).getOrThrow()
        }
    }
}