package com.nikealarm.nikedrawalarm.presentation.upcomingScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.nikealarm.nikedrawalarm.domain.usecase.GetPagingUpcomingUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class UpcomingViewModel @Inject constructor(
    private val getPagingUpcomingUseCase: GetPagingUpcomingUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(UpcomingUiState())
    val uiState = _uiState
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = _uiState.value
        )

    init {
        _uiState.update {
            it.copy(products = getPagingUpcomingUseCase().cachedIn(viewModelScope))
        }
    }
}