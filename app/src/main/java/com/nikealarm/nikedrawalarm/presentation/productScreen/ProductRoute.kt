package com.nikealarm.nikedrawalarm.presentation.productScreen

import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nikealarm.nikedrawalarm.domain.model.Product
import com.nikealarm.nikedrawalarm.presentation.ui.DisposableEffectWithLifeCycle
import kotlinx.coroutines.flow.collectLatest

@Composable
fun ProductRoute(
    viewModel: ProductViewModel = hiltViewModel(),
    navigateDetailScreen: (Product) -> Unit,
    onCreate: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val listState = rememberLazyGridState()

    DisposableEffectWithLifeCycle(
        onCreate = onCreate,
        onDispose = { }
    )

    LaunchedEffect(key1 = viewModel.uiEvent) {
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                is ProductUiEvent.ProductItemClick -> {
                    navigateDetailScreen(event.product)
                }

                is ProductUiEvent.ChangeProductCategory -> {
                    viewModel.changeCategory(event.selectedCategory)
                }

                is ProductUiEvent.ChangedProductCategory -> {
                    listState.scrollToItem(0)
                }
            }
        }
    }

    ProductScreen(
        state = state,
        listState = listState,
        onEvent = viewModel::handelEvent
    )
}