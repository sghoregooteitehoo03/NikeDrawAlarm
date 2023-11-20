package com.nikealarm.nikedrawalarm.presentation.collectionDetailScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nikealarm.nikedrawalarm.domain.model.Product
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class CollectionDetailViewModel @Inject constructor() : ViewModel() {
    private val _state = MutableStateFlow(CollectionDetailUiState())
    val state = _state.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = _state.value
    )

    fun initValue(product: Product?) {
        if (_state.value.product == null) {
            _state.update {
                it.copy(
                    product = product
                )
            }
        }
    }
}