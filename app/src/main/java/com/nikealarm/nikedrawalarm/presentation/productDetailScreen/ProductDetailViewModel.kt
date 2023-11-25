package com.nikealarm.nikedrawalarm.presentation.productDetailScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nikealarm.nikedrawalarm.data.model.entity.NotificationEntity
import com.nikealarm.nikedrawalarm.domain.model.ProductInfo
import com.nikealarm.nikedrawalarm.domain.usecase.GetAllowNotifyUseCase
import com.nikealarm.nikedrawalarm.domain.usecase.GetFavoriteUseCase
import com.nikealarm.nikedrawalarm.domain.usecase.GetNotificationUseCase
import com.nikealarm.nikedrawalarm.domain.usecase.GetProductInfoUseCase
import com.nikealarm.nikedrawalarm.domain.usecase.InsertFavoriteUseCase
import com.nikealarm.nikedrawalarm.domain.usecase.InsertLatestUseCase
import com.nikealarm.nikedrawalarm.domain.usecase.SetNotificationUseCase
import com.nikealarm.nikedrawalarm.util.AlarmBuilder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    private val alarmBuilder: AlarmBuilder,
    private val getProductUseCase: GetProductInfoUseCase,
    private val getFavoriteUseCase: GetFavoriteUseCase,
    private val getAllowNotifyUseCase: GetAllowNotifyUseCase,
    private val getNotificationUseCase: GetNotificationUseCase,
    private val insertFavoriteUseCase: InsertFavoriteUseCase,
    private val insertLatestUseCase: InsertLatestUseCase,
    private val setNotificationUseCase: SetNotificationUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProductDetailUiState())
    val uiState = _uiState
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = _uiState.value
        )

    fun loadProduct(
        productId: String,
        slug: String,
        onNotificationChange: (NotificationEntity?) -> Unit
    ) = viewModelScope.launch {
        if (_uiState.value.productInfo == null) {
            // 제품 정보를 API를 통해 로드해서 가져옴
            val productInfo = getProductUseCase(productId, slug)
            initValue(productInfo, onNotificationChange)
        }
    }

    fun initValue(
        productInfo: ProductInfo?,
        onNotificationChange: (NotificationEntity?) -> Unit
    ) {
        if (_uiState.value.productInfo == null && productInfo != null) {
            val productId = productInfo.productId

            viewModelScope.launch {
                insertLatestUseCase(productInfo) // 최근에 본 제품 추가

                combine(
                    getAllowNotifyUseCase(),
                    getFavoriteUseCase(productId),
                    getNotificationUseCase(productId)
                ) { isAllowNotify, favorite, notification ->
                    val notificationEntity = if (productInfo.eventDate != 0L) { // 이벤트 중인 상품일 때
                        if (alarmBuilder.isExistProductAlarm(productId) // 알람 설정된 상품일 경우
                            && notification != null
                        ) {
                            notification
                        } else { // 알람 설정이 안된 상품일 경우
                            NotificationEntity(productId, 0L, 0L, 0L)
                        }
                    } else {
                        null
                    }

                    onNotificationChange(notificationEntity)
                    _uiState.update {
                        it.copy(
                            productInfo = productInfo,
                            isAllowNotify = isAllowNotify,
                            isFavorite = favorite != null,
                            notificationEntity = notificationEntity,
                            isLoading = false
                        )
                    }
                }.collect()
            }
        }
    }

    fun clickFavorite(productInfo: ProductInfo?) = viewModelScope.launch {
        if (productInfo != null) {
            insertFavoriteUseCase(
                productInfo = productInfo,
                isFavorite = _uiState.value.isFavorite
            )
        }
    }

    fun setNotification(notificationTime: Long, isNotAllowNotify: () -> Unit) =
        viewModelScope.launch {
            if (_uiState.value.isAllowNotify == true) { // 알림 허용을 한 경우
                val productInfo = _uiState.value.productInfo
                if (productInfo != null) {
                    setNotificationUseCase(productInfo, notificationTime)
                }
            } else { // 알림 허용을 하지 않은 경우
                isNotAllowNotify()
            }
        }
}