package com.nikealarm.nikedrawalarm.presentation.favoriteMoreScreen

import androidx.paging.PagingData
import com.nikealarm.nikedrawalarm.domain.model.JoinedProduct
import com.nikealarm.nikedrawalarm.domain.model.JoinedProductType
import kotlinx.coroutines.flow.Flow

data class FavoriteMoreUiState(
    val products: Flow<PagingData<JoinedProduct>>? = null,
    val sendType: JoinedProductType? = null
)
