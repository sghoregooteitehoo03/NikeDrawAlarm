package com.nikealarm.nikedrawalarm.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Collection(
    val id: String,
    val title: String,
    val subTitle: String,
    val thumbnailImage: String,
    val price: String,
    val explains: String,
    val url: String
)
