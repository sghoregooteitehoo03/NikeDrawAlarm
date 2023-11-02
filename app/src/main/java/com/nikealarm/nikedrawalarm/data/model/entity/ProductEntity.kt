package com.nikealarm.nikedrawalarm.data.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ProductEntity(
    @PrimaryKey
    val productId: String,
    val title: String,
    val subTitle: String,
    val price: Int,
    val thumbnailImage: String,
    val eventDate: Long,
    val url: String,
    val category: String
)
