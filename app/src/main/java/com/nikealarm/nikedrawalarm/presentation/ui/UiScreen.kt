package com.nikealarm.nikedrawalarm.presentation.ui

import com.nikealarm.nikedrawalarm.R

sealed class UiScreen(
    val route: String,
    val bottomSelectedIcon: Int = -1,
    val bottomUnSelectedIcon: Int = -1,
) {
    data object ProductScreen : UiScreen(
        route = UiScreenName.PRODUCT_SCREEN,
        bottomSelectedIcon = R.drawable.product,
        bottomUnSelectedIcon = R.drawable.product_border
    )

    data object UpcomingScreen : UiScreen(
        route = UiScreenName.UPCOMING_SCREEN,
        bottomSelectedIcon = R.drawable.event,
        bottomUnSelectedIcon = R.drawable.event_border
    )

    data object FavoriteScreen : UiScreen(
        route = UiScreenName.FAVORITE_SCREEN,
        bottomSelectedIcon = R.drawable.favorite,
        bottomUnSelectedIcon = R.drawable.favorite_border
    )

    data object FavoriteMoreScreen : UiScreen(route = UiScreenName.FAVORITE_MORE_SCREEN)

    data object ProductDetailScreen : UiScreen(route = UiScreenName.PRODUCT_DETAIL_SCREEN)

    data object LoadProductDetailScreen :
        UiScreen(route = UiScreenName.LOAD_PRODUCT_DETAIL_SCREEN + "/{productId}")

    data object CollectionDetailScreen : UiScreen(route = UiScreenName.COLLECTION_DETAIL_SCREEN)
}

object UiScreenName {
    const val PRODUCT_SCREEN = "Product"
    const val UPCOMING_SCREEN = "Upcoming"
    const val FAVORITE_SCREEN = "Favorite"
    const val FAVORITE_MORE_SCREEN = "FavoriteMore"
    const val PRODUCT_DETAIL_SCREEN = "ProductDatail"
    const val LOAD_PRODUCT_DETAIL_SCREEN = "LoadProductDetail"
    const val COLLECTION_DETAIL_SCREEN = "CollectionDetail"
}