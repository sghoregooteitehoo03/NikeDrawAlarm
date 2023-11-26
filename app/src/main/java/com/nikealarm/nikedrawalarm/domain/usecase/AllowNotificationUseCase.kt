package com.nikealarm.nikedrawalarm.domain.usecase

import com.nikealarm.nikedrawalarm.data.repository.ProductDatabaseRepository
import com.nikealarm.nikedrawalarm.presentation.settingScreen.ClearProductType
import javax.inject.Inject

class AllowNotificationUseCase @Inject constructor(
    private val databaseRepository: ProductDatabaseRepository,
    private val clearProductUseCase: ClearProductUseCase,
    private val allowDrawNotifyUseCase: AllowDrawNotifyUseCase
) {

    suspend operator fun invoke(isAllow: Boolean) {
        if (!isAllow) { // 알림을 해제하는 경우 기존에 설정 된 알림들을 지움
            clearProductUseCase(ClearProductType.ClearNotifyProduct)
            allowDrawNotifyUseCase(false)
        }
        databaseRepository.setAllowNotification(isAllow)
    }
}