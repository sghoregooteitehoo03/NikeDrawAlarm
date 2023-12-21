package com.nikealarm.nikedrawalarm.presentation.favoriteMoreScreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nikealarm.nikedrawalarm.data.model.entity.ProductEntity

@Composable
fun FavoriteMoreRoute(
    viewModel: FavoriteMoreViewModel = hiltViewModel(),
    onProductClick: (ProductEntity) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    FavoriteMoreScreen(
        state = uiState,
        onProductClick = onProductClick
    )
}