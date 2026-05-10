package com.nikealarm.core.network.model

data class ProductInfo(
    val merchProduct: MerchProduct,
    val merchPrice: MerchPrice,
    val launchView: LaunchView?,
    val skus: List<Skus>?
)
