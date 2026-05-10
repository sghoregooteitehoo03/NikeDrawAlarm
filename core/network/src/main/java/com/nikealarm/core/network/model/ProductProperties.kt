package com.nikealarm.core.network.model

data class ProductProperties(
    val actions: List<Actions>,
    val title: String,
    val subtitle: String,
    val portraitURL: String,
    val squarishURL: String,
    val jsonBody: JsonBody?
)
