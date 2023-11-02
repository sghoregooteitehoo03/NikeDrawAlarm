package com.nikealarm.nikedrawalarm.presentation.ui

import androidx.compose.ui.res.stringResource
import com.nikealarm.nikedrawalarm.R
import com.nikealarm.nikedrawalarm.util.Constants

sealed class UiScreen(
    val route: String
) {
    object ProductScreen : UiScreen(route = UiScreenName.PRODUCT_SCREEN)
    object ProductDetailScreen : UiScreen(route = UiScreenName.PRODUCT_DETAIL_SCREEN)
    object CollectionDetailScreen : UiScreen(route = UiScreenName.COLLECTION_DETAIL_SCREEN)
}

object UiScreenName {
    const val PRODUCT_SCREEN = "Product"
    const val PRODUCT_DETAIL_SCREEN = "ProductDatail"
    const val COLLECTION_DETAIL_SCREEN = "CollectionDetailScreen"
}