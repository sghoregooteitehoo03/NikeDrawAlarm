package com.nikealarm.core.domain.usecase

import com.nikealarm.core.common.Result
import com.nikealarm.core.domain.repository.DatabaseRepository
import com.nikealarm.core.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetAllowNotifyUseCase @Inject constructor(
    private val productRepository: ProductRepository,
    private val databaseRepository: DatabaseRepository,
    private val allowNotificationUseCase: AllowNotificationUseCase
) {
    operator fun invoke(): Flow<Boolean> {
        return databaseRepository.getAllowNotification()
            .map { isAllow ->
                val permissionResult = productRepository.checkAlarmPermissions()
                val hasPermission = if (permissionResult is Result.ResultSuccess) {
                    permissionResult.data
                } else {
                    false // 에러가 나면 Flow를 깨지 않고 그냥 권한이 없는 것으로 간주!
                }

                if (hasPermission) {
                    isAllow // 권한이 있으면 DB 상태 그대로 반환!
                } else {
                    // OS 권한이 꺼졌는데 앱 내부 DB는 켜져 있다면? -> 동기화를 위해 강제 종료!
                    if (isAllow) {
                        // TODO: 부수효과 제거
                        allowNotificationUseCase(false) // (suspend 함수도 map 안에서 호출 가능합니다)
                    }
                    false // 권한이 없으므로 무조건 false 반환!
                }
            }
            .distinctUntilChanged() // 여러번 방출 방지
    }
}