package com.nikealarm.core.network.model.product

data class MerchProduct(
    val id: String,
    val status: String,
    val commerceStartDate: String?,
    val commerceEndDate: String?
)
