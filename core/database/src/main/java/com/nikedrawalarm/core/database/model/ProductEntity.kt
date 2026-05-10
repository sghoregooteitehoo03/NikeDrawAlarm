package com.nikedrawalarm.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ProductEntity(
    @PrimaryKey
    @ColumnInfo("Id")
    val productId: String,
    val title: String,
    val subTitle: String,
    val price: Int,
    val thumbnailImage: String,
    val eventDate: Long,
    val url: String,
    val category: String
)
