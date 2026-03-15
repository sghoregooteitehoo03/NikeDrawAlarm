package com.nikealarm.core.model

sealed class ClearProductType {
    data object ClearLatestProduct : ClearProductType()
    data object ClearNotifyProduct : ClearProductType()
    data object ClearFavoriteProduct : ClearProductType()
}