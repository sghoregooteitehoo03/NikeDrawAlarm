package com.nikealarm.nikedrawalarm.presentation.ui

sealed class UiScreen(
    val route: String
) {
    object ProductScreen : UiScreen(route = UiScreenName.PRODUCT_SCREEN)
    object ProductDetailScreen : UiScreen(route = UiScreenName.PRODUCT_DETAIL_SCREEN)
    object LoadProductDetailScreen :
        UiScreen(route = UiScreenName.LOAD_PRODUCT_DETAIL_SCREEN + "/{productId}")

    object CollectionDetailScreen : UiScreen(route = UiScreenName.COLLECTION_DETAIL_SCREEN)
}

object UiScreenName {
    const val PRODUCT_SCREEN = "Product"
    const val PRODUCT_DETAIL_SCREEN = "ProductDatail"
    const val LOAD_PRODUCT_DETAIL_SCREEN = "LoadProductDetail"
    const val COLLECTION_DETAIL_SCREEN = "CollectionDetail"
}