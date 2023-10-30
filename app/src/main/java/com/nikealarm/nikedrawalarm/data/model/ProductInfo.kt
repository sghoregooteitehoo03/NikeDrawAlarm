package com.nikealarm.nikedrawalarm.data.model

data class ProductInfo(
    val merchProduct: MerchProduct,
    val merchPrice: MerchPrice,
    val launchView: LaunchView?,
    val skus: List<Skus>?
)
