package com.nikealarm.core.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

data class JoinedProduct(
    val productInfo: ProductInfo,
    val explains: String
)

@Serializable
sealed class JoinedProductType(val text: String) {
    @Serializable
    @SerialName("LatestProduct")
    data object LatestProduct : JoinedProductType("최근에 본 제품")

    @Serializable
    @SerialName("NotifyProduct")
    data object NotifyProduct : JoinedProductType("알림 설정한 제품")

    @Serializable
    @SerialName("FavoriteProduct")
    data object FavoriteProduct : JoinedProductType("좋아요 한 제품")
}
