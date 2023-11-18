package com.nikealarm.nikedrawalarm.presentation.productDetailScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nikealarm.nikedrawalarm.domain.model.ProductInfo
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
    private val getProductInfoUseCase: GetProductInfoUseCase,
    private val getFavoriteUseCase: GetFavoriteUseCase,
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

    // TODO: 컬렉션 제품 로드 시 오류발생
    fun loadProduct(productId: String) = viewModelScope.launch {
        val productInfo = getProductInfoUseCase(productId)
        initValue(productInfo)
    }

    fun initValue(productInfo: ProductInfo?) {
        if (productInfo != null) {
            val productId = productInfo.productId

            viewModelScope.launch {
                insertLatestUseCase(productInfo) // 최근에 본 제품 추가
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
                            },
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

    // 상품 알림 설정
    fun setNotification(notificationTime: Long) = viewModelScope.launch {
        val productInfo = _uiState.value.productInfo
        if (productInfo != null) {
            setNotificationUseCase(productInfo, notificationTime)
        }
    }
}