package com.nikealarm.nikedrawalarm.presentation.ui

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.nikealarm.nikedrawalarm.data.model.entity.NotificationEntity
import com.nikealarm.nikedrawalarm.domain.model.JoinedProductCategory
import com.nikealarm.nikedrawalarm.domain.model.Product
import com.nikealarm.nikedrawalarm.domain.model.ProductInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class GlobalViewModel @Inject constructor() : ViewModel() {
    // 데이터 전달 용
    private var product: Product? = null
    private var productInfo: ProductInfo? = null
    private var joinedProductCategory: JoinedProductCategory? = null

    fun sendProductData(_product: Product?) {
        product = _product
    }

    fun sendProductInfoData(_productInfo: ProductInfo?) {
        productInfo = _productInfo
    }

    fun sendJoinedProductCategory(_joinedProductCategory: JoinedProductCategory?) {
        joinedProductCategory = _joinedProductCategory
    }

    fun getProductData() = product

    fun getProductInfoData() = productInfo

    fun getJoinedProductCategory() = joinedProductCategory

    fun clearData() {
        product = null
        productInfo = null
        joinedProductCategory = null
    }

    // State
    private var _notificationEntity: MutableState<NotificationEntity?> = mutableStateOf(null)
    val notificationEntity: State<NotificationEntity?> = _notificationEntity

    private val _dialogScreen: MutableState<DialogScreen> =
        mutableStateOf(DialogScreen.DialogDismiss)
    val dialogScreen: State<DialogScreen> = _dialogScreen

    fun dialogOpen(dialog: DialogScreen) {
        _dialogScreen.value = dialog
    }

    fun setNotificationEntity(data: NotificationEntity?) {
        _notificationEntity.value = data
    }
}