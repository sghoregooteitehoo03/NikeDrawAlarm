package com.nikealarm.nikedrawalarm.presentation.productDetailScreen

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.nikealarm.nikedrawalarm.presentation.ui.DisposableEffectWithLifeCycle

@Composable
fun ProductDetailRoute(
    viewModel: ProductDetailViewModel = hiltViewModel()
) {
    DisposableEffectWithLifeCycle(
        onCreate = { Log.i("Check", "ON_CREATE") },
        onDispose = { Log.i("Check", "ON_DISPOS") }
    )
}