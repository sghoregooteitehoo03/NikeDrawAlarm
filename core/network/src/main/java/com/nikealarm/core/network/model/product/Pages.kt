package com.nikealarm.core.network.model.product

data class Pages(
    var prev: String,
    var next: String,
    var totalPages: Int,
    var totalResources: Int
)
