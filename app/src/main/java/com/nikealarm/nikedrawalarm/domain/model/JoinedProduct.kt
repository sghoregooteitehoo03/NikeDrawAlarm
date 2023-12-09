package com.nikealarm.nikedrawalarm.domain.model

import com.nikealarm.nikedrawalarm.data.model.entity.ProductEntity

data class JoinedProduct(
    val productEntity: ProductEntity,
    val explains: String
)

sealed class JoinedProductType(val text: String) {
    data object LatestProduct : JoinedProductType("최근에 본 제품")
    data object NotifyProduct: JoinedProductType("알림 설정한 제품")
    data object FavoriteProduct: JoinedProductType("좋아요 한 제품")
}
