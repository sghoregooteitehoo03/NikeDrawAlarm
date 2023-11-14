package com.nikealarm.nikedrawalarm.presentation.productDetailScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nikealarm.nikedrawalarm.domain.model.ProductInfo
import com.nikealarm.nikedrawalarm.domain.usecase.GetFavoriteUseCase
import com.nikealarm.nikedrawalarm.domain.usecase.GetNotificationUseCase
import com.nikealarm.nikedrawalarm.domain.usecase.InsertFavoriteUseCase
import com.nikealarm.nikedrawalarm.domain.usecase.SetNotificationUseCase
import com.nikealarm.nikedrawalarm.util.AlarmBuilder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    private val alarmBuilder: AlarmBuilder,
    private val getFavoriteUseCase: GetFavoriteUseCase,
    private val getNotificationUseCase: GetNotificationUseCase,
    private val insertFavoriteUseCase: InsertFavoriteUseCase,
    private val setNotificationUseCase: SetNotificationUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProductDetailUiState())
    val uiState = _uiState
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = _uiState.value
        )

    fun initValue(productInfo: ProductInfo?) {
        val productId = productInfo?.productId ?: ""

        combine(
            getFavoriteUseCase(productId),
            getNotificationUseCase(productId)
        ) { favorite, notification ->
            _uiState.update {
                it.copy(
                    productInfo = productInfo,
                    isFavorite = favorite != null,
                    notificationEntity = if (alarmBuilder.isExistProductAlarm(productId)) {
                        notification
                    } else {
                        null
                    }
                )
            }
        }.launchIn(viewModelScope)
    }

    fun clickFavorite(productInfo: ProductInfo?) = viewModelScope.launch {
        if (productInfo != null) {
            insertFavoriteUseCase(
                productInfo = productInfo,
                isFavorite = _uiState.value.isFavorite
            )
        }
    }

    // 상품 알림 설정
    fun setNotification(notificationTime: Long) = viewModelScope.launch {
        val productInfo = _uiState.value.productInfo
        if (productInfo != null) {
            setNotificationUseCase(productInfo, notificationTime)
        }
    }
}