package com.nikealarm.nikedrawalarm.presentation.ui

import androidx.compose.ui.res.stringResource
import com.nikealarm.nikedrawalarm.R
import com.nikealarm.nikedrawalarm.util.Constants

sealed class UiScreen(
    val route: String
) {
    object ProductScreen : UiScreen(route = Constants.PRODUCT_SCREEN)
    object ProductDetailScreen : UiScreen(route = Constants.PRODUCT_DETAIL_SCREEN)
}