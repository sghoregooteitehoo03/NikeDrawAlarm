package com.nikealarm.nikedrawalarm.presentation.upcomingScreen

import androidx.paging.PagingData
import com.nikealarm.nikedrawalarm.domain.model.ProductInfo
import kotlinx.coroutines.flow.Flow

data class UpcomingUiState(
    val products: Flow<PagingData<ProductInfo>>? = null
)
