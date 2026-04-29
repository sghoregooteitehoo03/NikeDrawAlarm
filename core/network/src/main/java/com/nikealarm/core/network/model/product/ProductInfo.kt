package com.nikealarm.core.network.model.product

data class ProductInfo(
    val merchProduct: MerchProduct,
    val merchPrice: MerchPrice,
    val launchView: LaunchView?,
    val skus: List<Skus>?
)
