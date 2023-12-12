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

    data object SettingScreen : UiScreen(route = UiScreenName.SETTING_SCREEN)

    data object FavoriteMoreScreen : UiScreen(
        route = UiScreenName.FAVORITE_MORE_SCREEN + "?type={type}"
    )

    data object ProductDetailScreen : UiScreen(
        route = UiScreenName.PRODUCT_DETAIL_SCREEN + "?productInfo={productInfo}"
    )

    data object LoadProductDetailScreen :
        UiScreen(route = UiScreenName.LOAD_PRODUCT_DETAIL_SCREEN + "?id={productId}&slug={productSlug}")

    data object CollectionDetailScreen : UiScreen(
        route = UiScreenName.COLLECTION_DETAIL_SCREEN + "?product={product}"
    )
}

object UiScreenName {
    const val PRODUCT_SCREEN = "Product"
    const val UPCOMING_SCREEN = "Upcoming"
    const val FAVORITE_SCREEN = "Favorite"
    const val SETTING_SCREEN = "Setting"
    const val FAVORITE_MORE_SCREEN = "FavoriteMore"
    const val PRODUCT_DETAIL_SCREEN = "ProductDetail"
    const val LOAD_PRODUCT_DETAIL_SCREEN = "LoadProductDetail"
    const val COLLECTION_DETAIL_SCREEN = "CollectionDetail"
}