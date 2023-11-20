package com.nikealarm.nikedrawalarm.presentation.favoriteScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nikealarm.nikedrawalarm.domain.usecase.GetCombineProductsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val getCombineProductsUseCase: GetCombineProductsUseCase
) : ViewModel() {
    val uiState = getCombineProductsUseCase(
        latestLimit = 8,
        notifyLimit = 3,
        favoriteLimit = 3,
        transform = { latest, notify, favorite ->
            FavoriteUiState.Success(
                latestProducts = latest,
                notifyProducts = notify,
                favoriteProducts = favorite
            )
        }
    ).stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = FavoriteUiState.Loading
    )
}