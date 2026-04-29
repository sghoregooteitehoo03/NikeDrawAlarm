package com.nikealarm.core.network.model.product

data class Nodes(
    val nodes: List<ImageNodes>?,

    val properties: ProductProperties,
    val subType: String
)
