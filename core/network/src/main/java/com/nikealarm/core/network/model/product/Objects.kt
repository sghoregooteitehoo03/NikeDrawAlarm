package com.nikealarm.core.network.model.product

data class Objects(
    val id: String,
    val productInfo: List<ProductInfo>?,
    val publishedContent: PublishedContent
)