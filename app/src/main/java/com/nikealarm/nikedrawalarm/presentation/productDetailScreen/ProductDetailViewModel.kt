package com.nikealarm.nikedrawalarm.presentation.productDetailScreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nikealarm.nikedrawalarm.domain.model.Product
import com.nikealarm.nikedrawalarm.domain.model.ProductInfo
import com.nikealarm.nikedrawalarm.domain.usecase.GetFavoriteUseCase
import com.nikealarm.nikedrawalarm.domain.usecase.InsertFavoriteUseCase
import com.nikealarm.nikedrawalarm.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    private val getFavoriteUseCase: GetFavoriteUseCase,
    private val insertFavoriteUseCase: InsertFavoriteUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProductDetailUiState())
    val uiState = _uiState
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = _uiState.value
        )

    fun initValue(productInfo: ProductInfo?) {
        getFavoriteUseCase(productId = productInfo?.productId ?: "")
            .onEach { result ->
                when (result) {
                    is Result.Success -> _uiState.update {
                        it.copy(
                            productInfo = productInfo,
                            isFavorite = result.data != null
                        )
                    }

                    else -> {

                    }
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
}