package com.nikealarm.nikedrawalarm.presentation.productScreen

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.nikealarm.nikedrawalarm.domain.model.Product
import com.nikealarm.nikedrawalarm.presentation.ui.DisposableEffectWithLifeCycle

@Composable
fun ProductRoute(
    viewModel: ProductViewModel = hiltViewModel(),
    onProductItemClick: (Product) -> Unit,
    onCreate: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    DisposableEffectWithLifeCycle(
        onCreate = onCreate,
        onDispose = { }
    )

    ProductScreen(
        state = state,
        onProductItemClick = onProductItemClick,
        onCategoryItemClick = viewModel::changeCategory
    )
}