package com.nikealarm.nikedrawalarm.presentation.favoriteScreen

import com.nikealarm.nikedrawalarm.data.model.entity.FavoriteProductEntity
import com.nikealarm.nikedrawalarm.data.model.entity.LatestProductEntity
import com.nikealarm.nikedrawalarm.data.model.entity.NotifyProductEntity

sealed interface FavoriteUiState {
    data class Success(
        val latestProducts: List<LatestProductEntity>,
        val notifyProducts: List<NotifyProductEntity>,
        val favoriteProducts: List<FavoriteProductEntity>,
    ) : FavoriteUiState

    data object Loading : FavoriteUiState
}