package com.nikealarm.nikedrawalarm.presentation.productScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.paging.filter
import androidx.paging.flatMap
import androidx.paging.map
import com.nikealarm.nikedrawalarm.data.repository.ProductRepository
import com.nikealarm.nikedrawalarm.domain.model.ProductCategory
import com.nikealarm.nikedrawalarm.domain.usecase.GetPagingProductsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val getProductsUseCase: GetPagingProductsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ProductScreenState())
    val state = _state
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = _state.value
        )

    init {
        _state.update {
            it.copy(
                products = getProductsUseCase(ProductCategory.All).cachedIn(viewModelScope)
            )
        }
    }

    fun changeCategory(selectedCategory: ProductCategory) = viewModelScope.launch {
        _state.update {
            it.copy(
                products = getProductsUseCase(selectedCategory).cachedIn(viewModelScope),
                selectedCategory = selectedCategory
            )
        }
    }
}