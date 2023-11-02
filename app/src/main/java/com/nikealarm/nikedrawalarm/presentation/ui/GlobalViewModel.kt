package com.nikealarm.nikedrawalarm.presentation.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nikealarm.nikedrawalarm.data.model.entity.NotificationEntity
import com.nikealarm.nikedrawalarm.domain.model.Product
import com.nikealarm.nikedrawalarm.domain.model.ProductInfo
import com.nikealarm.nikedrawalarm.domain.usecase.GetNotificationUseCase
import com.nikealarm.nikedrawalarm.domain.usecase.SetNotificationUseCase
import com.nikealarm.nikedrawalarm.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GlobalViewModel @Inject constructor(
    private val getNotificationUseCase: GetNotificationUseCase,
    private val setNotificationUseCase: SetNotificationUseCase
) : ViewModel() {
    // 데이터 전달 용
    private var product: Product? = null
    private var productInfo: ProductInfo? = null

    private var _notificationEntity: MutableStateFlow<NotificationEntity?> =
        MutableStateFlow(null) // AlarmAction 전용 데이터
    val notificationEntity = _notificationEntity
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = _notificationEntity.value
        )


    fun sendProductData(_product: Product?) {
        product = _product
    }

    fun sendProductInfoData(_productInfo: ProductInfo?) {
        productInfo = _productInfo
    }

    fun getProductData() = product

    fun getProductInfoData() = productInfo

    fun getNotificationData(productId: String) {
        getNotificationUseCase(productId).onEach { result ->
            when (result) {
                is Result.Success -> {
                    _notificationEntity.update {
                        result.data
                    }
                }

                else -> {}
            }
        }.launchIn(viewModelScope)
    }

    fun setNotification(notificationTime: Long) = viewModelScope.launch {
        val productInfo = productInfo
        if (productInfo != null) {
            setNotificationUseCase(productInfo, notificationTime)
        }
    }
}