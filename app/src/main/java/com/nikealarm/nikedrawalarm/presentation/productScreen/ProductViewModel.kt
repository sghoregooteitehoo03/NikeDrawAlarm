package com.nikealarm.nikedrawalarm.presentation.productScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.nikealarm.nikedrawalarm.domain.model.ProductCategory
import com.nikealarm.nikedrawalarm.domain.usecase.GetPagingProductsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val getProductsUseCase: GetPagingProductsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductUiState())
    private val _uiEvent = MutableSharedFlow<ProductUiEvent>()

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
        _uiState.update {
            it.copy(
                allProducts = getProductsUseCase(ProductCategory.All).cachedIn(viewModelScope),
                comingProducts = getProductsUseCase(ProductCategory.Coming).cachedIn(viewModelScope),
                drawProducts = getProductsUseCase(ProductCategory.Draw)
                    .cachedIn(viewModelScope)
            )
        }
    }

    fun handelEvent(event: ProductUiEvent) =
        viewModelScope.launch {
            _uiEvent.emit(event)
        }

    fun changeCategory(
        selectedCategory: ProductCategory
    ) = viewModelScope.launch {
        _uiState.update {
            it.copy(
                selectedCategory = selectedCategory
            )
        }
        _uiEvent.emit(ProductUiEvent.ChangedProductCategory) // 카테고리가 변경되었음을 알림
    }
}