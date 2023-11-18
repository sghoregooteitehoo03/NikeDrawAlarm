package com.nikealarm.nikedrawalarm.presentation.favoriteMoreScreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nikealarm.nikedrawalarm.data.model.entity.ProductEntity
import com.nikealarm.nikedrawalarm.domain.model.JoinedProductCategory
import com.nikealarm.nikedrawalarm.presentation.ui.DisposableEffectWithLifeCycle

@Composable
fun FavoriteMoreRoute(
    viewModel: FavoriteMoreViewModel = hiltViewModel(),
    sendCategory: JoinedProductCategory?,
    onProductClick: (ProductEntity) -> Unit,
    onDispose: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    DisposableEffectWithLifeCycle(
        onCreate = { viewModel.initValue(sendCategory) },
        onDispose = onDispose
    )

    FavoriteMoreScreen(
        state = uiState,
        onProductClick = onProductClick
    )
}