package com.nikealarm.nikedrawalarm.presentation.favoriteScreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nikealarm.nikedrawalarm.data.model.entity.ProductEntity
import com.nikealarm.nikedrawalarm.domain.model.JoinedProductType
import com.nikealarm.nikedrawalarm.presentation.ui.DisposableEffectWithLifeCycle

@Composable
fun FavoriteRoute(
    viewModel: FavoriteViewModel = hiltViewModel(),
    onProductClick: (ProductEntity) -> Unit,
    onMoreClick: (JoinedProductType) -> Unit,
    onCreate: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    DisposableEffectWithLifeCycle(
        onCreate = onCreate,
        onDispose = { }
    )

    FavoriteScreen(
        state = uiState,
        onProductClick = onProductClick,
        onMoreClick = onMoreClick
    )
}