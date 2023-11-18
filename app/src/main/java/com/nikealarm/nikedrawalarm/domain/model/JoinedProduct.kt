package com.nikealarm.nikedrawalarm.domain.model

import com.nikealarm.nikedrawalarm.data.model.entity.ProductEntity

data class JoinedProduct(
    val productEntity: ProductEntity,
    val explains: String
)

sealed class JoinedProductCategory(val text: String) {
    data object LatestProduct : JoinedProductCategory("최근에 본 제품")
    data object NotifyProduct: JoinedProductCategory("알림 설정한 제품")
    data object FavoriteProduct: JoinedProductCategory("좋아요 한 제품")
}
