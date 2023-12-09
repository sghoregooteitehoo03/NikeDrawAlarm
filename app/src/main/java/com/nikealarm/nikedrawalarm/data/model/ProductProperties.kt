package com.nikealarm.nikedrawalarm.data.model

data class ProductProperties(
    val actions: List<Actions>,
    val title: String,
    val subtitle: String,
    val portraitURL: String,
    val squarishURL: String,
    val jsonBody: JsonBody?
)
