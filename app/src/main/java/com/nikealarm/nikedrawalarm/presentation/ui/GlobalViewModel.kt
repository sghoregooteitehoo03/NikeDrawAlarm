package com.nikealarm.nikedrawalarm.presentation.ui

import androidx.lifecycle.ViewModel
import com.nikealarm.nikedrawalarm.domain.model.Product

class GlobalViewModel() : ViewModel() {
    // 데이터 전달 용
    private var product: Product? = null

    fun sendProductData(_product: Product?) {
        product = _product
    }

    fun getProductData() = product
}