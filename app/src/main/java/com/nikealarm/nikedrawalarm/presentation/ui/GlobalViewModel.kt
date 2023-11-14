package com.nikealarm.nikedrawalarm.presentation.ui

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
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
class GlobalViewModel @Inject constructor() : ViewModel() {
    // 데이터 전달 용
    private var product: Product? = null
    private var productInfo: ProductInfo? = null

    // State
    private var _notificationEntity: MutableState<NotificationEntity?> = mutableStateOf(null)
    val notificationEntity: State<NotificationEntity?> = _notificationEntity
    private val _isDialogOpen = mutableStateOf(false)
    val isDialogOpen: State<Boolean> = _isDialogOpen

    fun sendProductData(_product: Product?) {
        product = _product
    }

    fun sendProductInfoData(_productInfo: ProductInfo?) {
        productInfo = _productInfo
    }

    fun getProductData() = product

    fun getProductInfoData() = productInfo

    fun dialogOpen(isOpen: Boolean) {
        _isDialogOpen.value = isOpen
    }

    fun setNotificationEntity(data: NotificationEntity?) {
        _notificationEntity.value = data
    }
}