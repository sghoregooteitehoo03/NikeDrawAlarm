package com.nikealarm.nikedrawalarm.presentation.productScreen

import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nikealarm.nikedrawalarm.domain.model.Product
import com.nikealarm.nikedrawalarm.presentation.ui.ActionEvent
import com.nikealarm.nikedrawalarm.presentation.ui.DisposableEffectWithLifeCycle
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flattenMerge
import kotlinx.coroutines.flow.flowOf

@OptIn(ExperimentalCoroutinesApi::class)
@Composable
fun ProductRoute(
    viewModel: ProductViewModel = hiltViewModel(),
    actionEvent: SharedFlow<ActionEvent>,
    navigateDetailScreen: (Product) -> Unit,
    onCreate: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val listState = rememberLazyGridState()

    DisposableEffectWithLifeCycle(
        onCreate = onCreate,
        onDispose = { }
    )

    LaunchedEffect(key1 = true) {
        flowOf(actionEvent, viewModel.uiEvent)
            .flattenMerge()
            .collectLatest { event ->
                when (event) {
                    is ActionEvent.ActionSelectCategory -> {
                        viewModel.changeCategory(event.category)
                    }

                    is ProductUiEvent.ProductItemClick -> {
                        navigateDetailScreen(event.product)
                    }

                    is ProductUiEvent.ChangeProductCategory -> {
                        viewModel.changeCategory(event.selectedCategory)
                    }

                    is ProductUiEvent.ChangedProductCategory -> {
                        listState.scrollToItem(0)
                    }

                    else -> {}
                }
            }
    }

    ProductScreen(
        state = state,
        listState = listState,
        onEvent = viewModel::handelEvent
    )
}