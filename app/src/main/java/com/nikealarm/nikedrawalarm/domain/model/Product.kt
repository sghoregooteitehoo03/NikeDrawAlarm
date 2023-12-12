package com.nikealarm.nikedrawalarm.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Product(
    val collection: Collection?,
    val productInfoList: List<ProductInfo>
) {
}