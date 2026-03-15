package com.nikealarm.core.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
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
)

object ProductState {
    const val PRODUCT_STATUS_ACTIVE = "ACTVICE"
    const val PRODUCT_STATUS_INACTIVE = "INACTIVE"
}

@Serializable
sealed class ProductCategory(val text: String = "") {
    @Serializable
    @SerialName("All")
    data object All : ProductCategory("All")

    @Serializable
    @SerialName("Feed")
    data object Feed : ProductCategory("Feed")

    @Serializable
    @SerialName("SoldOut")
    data object SoldOut : ProductCategory("SoldOut")

    @Serializable
    @SerialName("Coming")
    data object Coming : ProductCategory("Coming")

    @Serializable
    @SerialName("Draw")
    data object Draw : ProductCategory("Draw")
}