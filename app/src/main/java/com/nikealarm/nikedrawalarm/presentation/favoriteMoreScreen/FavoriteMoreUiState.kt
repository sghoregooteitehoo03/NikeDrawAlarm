package com.nikealarm.nikedrawalarm.presentation.favoriteMoreScreen

import androidx.paging.PagingData
import com.nikealarm.nikedrawalarm.domain.model.JoinedProduct
import kotlinx.coroutines.flow.Flow

data class FavoriteMoreUiState(
    val products: Flow<PagingData<JoinedProduct>>? = null
)
