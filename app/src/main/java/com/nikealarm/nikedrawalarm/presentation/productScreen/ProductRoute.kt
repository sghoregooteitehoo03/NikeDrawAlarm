package com.nikealarm.nikedrawalarm.presentation.productScreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.nikealarm.nikedrawalarm.domain.model.Product

@Composable
fun ProductRoute(
    viewModel: ProductViewModel = hiltViewModel(),
    onProductItemClick: (Product) -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ProductScreen(
        state = state,
        onProductItemClick = onProductItemClick,
        onCategoryItemClick = viewModel::changeCategory
    )
}