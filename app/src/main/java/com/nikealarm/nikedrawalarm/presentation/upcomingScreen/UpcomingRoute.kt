package com.nikealarm.nikedrawalarm.presentation.upcomingScreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nikealarm.nikedrawalarm.domain.model.ProductInfo
import com.nikealarm.nikedrawalarm.presentation.ui.DisposableEffectWithLifeCycle

@Composable
fun UpcomingRoute(
    viewModel: UpcomingViewModel = hiltViewModel(),
    onProductClick: (ProductInfo) -> Unit,
    onCreate: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    DisposableEffectWithLifeCycle(
        onCreate = onCreate,
        onDispose = { }
    )

    UpcomingScreen(
        state = uiState,
        onProductClick = onProductClick
    )
}