package com.nikealarm.nikedrawalarm.presentation.favoriteScreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nikealarm.nikedrawalarm.data.model.entity.ProductEntity

@Composable
fun FavoriteRoute(
    viewModel: FavoriteViewModel = hiltViewModel(),
    onProductClick: (ProductEntity) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    FavoriteScreen(
        state = uiState,
        onProductClick = onProductClick
    )
}