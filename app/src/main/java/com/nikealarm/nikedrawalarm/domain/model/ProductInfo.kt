package com.nikealarm.nikedrawalarm.domain.model

import com.nikealarm.nikedrawalarm.data.model.entity.ProductEntity

data class ProductInfo(
    val productId: String,
    val title: String,
    val subTitle: String,
    val price: Int,
    val images: List<String>,
    val eventDate: Long,
    val explains: String,
    val sizes: List<String>,
    val url: String,
    val category: ProductCategory
) {
    fun getProductEntity() =
        ProductEntity(
            productId = productId,
            title = title,
            subTitle = subTitle,
            price = price,
            thumbnailImage = images[0],
            eventDate = eventDate,
            url = url,
            category = category.text
        )
}

object ProductState {
    const val PRODUCT_STATUS_ACTIVE = "ACTVICE"
    const val PRODUCT_STATUS_INACTIVE = "INACTIVE"
}

sealed class ProductCategory(val text: String = "") {
    object All : ProductCategory("All")
    object Feed : ProductCategory()
    object SoldOut : ProductCategory()
    object Coming : ProductCategory("Coming")
    object Draw : ProductCategory("Draw")
}