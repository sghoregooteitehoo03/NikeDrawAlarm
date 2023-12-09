package com.nikealarm.nikedrawalarm.presentation.favoriteMoreScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.nikealarm.nikedrawalarm.domain.model.JoinedProductType
import com.nikealarm.nikedrawalarm.domain.usecase.GetPagingJoinedProductUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class FavoriteMoreViewModel @Inject constructor(
    private val getPagingJoinedProductUseCase: GetPagingJoinedProductUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(FavoriteMoreUiState())
    val uiState = _uiState.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        initialValue = _uiState.value
    )

    fun initValue(joinedCategory: JoinedProductType?) {
        if (joinedCategory != null && _uiState.value.products == null)
            _uiState.update {
                it.copy(
                    products = getPagingJoinedProductUseCase(joinedCategory).cachedIn(viewModelScope),
                    sendType = joinedCategory
                )
            }
    }
}