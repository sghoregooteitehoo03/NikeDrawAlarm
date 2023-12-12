package com.nikealarm.nikedrawalarm.presentation.productDetailScreen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nikealarm.nikedrawalarm.domain.model.ProductInfo
import com.nikealarm.nikedrawalarm.domain.usecase.GetAllowNotifyUseCase
import com.nikealarm.nikedrawalarm.domain.usecase.GetFavoriteUseCase
import com.nikealarm.nikedrawalarm.domain.usecase.GetNotificationUseCase
import com.nikealarm.nikedrawalarm.domain.usecase.GetProductInfoUseCase
import com.nikealarm.nikedrawalarm.domain.usecase.InsertFavoriteUseCase
import com.nikealarm.nikedrawalarm.domain.usecase.InsertLatestUseCase
import com.nikealarm.nikedrawalarm.domain.usecase.SetNotificationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.lang.Exception
import java.lang.NullPointerException
import javax.inject.Inject

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    private val getProductInfoUseCase: GetProductInfoUseCase,
    private val getFavoriteUseCase: GetFavoriteUseCase,
    private val getAllowNotifyUseCase: GetAllowNotifyUseCase,
    private val getNotificationUseCase: GetNotificationUseCase,
    private val insertFavoriteUseCase: InsertFavoriteUseCase,
    private val insertLatestUseCase: InsertLatestUseCase,
    private val setNotificationUseCase: SetNotificationUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProductDetailUiState())
    private val _uiEvent = MutableSharedFlow<ProductDetailUiEvent>()

    val uiState = _uiState
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = _uiState.value
        )
    val uiEvent = _uiEvent.shareIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly
    )

    init {
        viewModelScope.launch {
            try {
                val productId = savedStateHandle.get<String>("productId")
                val slug = savedStateHandle.get<String>("productSlug")

                val productInfo = if (productId != null && slug != null) {
                    getProductInfoUseCase(productId, slug)
                } else {
                    val productInfoJson = savedStateHandle.get<String>("productInfo") ?: ""
                    Json.decodeFromString(ProductInfo.serializer(), productInfoJson)
                }

                if (productInfo != null) {
                    insertLatestUseCase(productInfo) // 최근에 본 제품 추가
                    combine(
                        getAllowNotifyUseCase(),
                        getFavoriteUseCase(productInfo.productId),
                        getNotificationUseCase(productInfo.productId, productInfo.eventDate)
                    ) { isAllowNotify, favorite, notifyEntity ->

                        _uiState.update {
                            it.copy(
                                productInfo = productInfo,
                                isAllowNotify = isAllowNotify,
                                isFavorite = favorite != null,
                                notificationEntity = notifyEntity,
                                isLoading = false
                            )
                        }
                        handelEvent(ProductDetailUiEvent.NotificationChange(notifyEntity))
                    }.collect()
                } else {
                    throw NullPointerException()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                handelEvent(ProductDetailUiEvent.Error("제품을 읽어오는 과정에서 오류가 발생하였습니다."))
            }
        }
    }

    fun handelEvent(onEvent: ProductDetailUiEvent) =
        viewModelScope.launch {
            _uiEvent.emit(onEvent)
        }

    fun clickFavorite() = viewModelScope.launch {
        val productInfo = _uiState.value.productInfo
        if (productInfo != null) {
            insertFavoriteUseCase(
                productInfo = productInfo,
                isFavorite = _uiState.value.isFavorite
            )
        }
    }

    fun setNotification(notificationTime: Long) =
        viewModelScope.launch {
            if (_uiState.value.isAllowNotify) { // 알림 허용을 한 경우
                val productInfo = _uiState.value.productInfo
                if (productInfo != null) {
                    setNotificationUseCase(productInfo, notificationTime)
                }
                handelEvent(ProductDetailUiEvent.SuccessInsertNotification(notificationTime))
            }
        }
}