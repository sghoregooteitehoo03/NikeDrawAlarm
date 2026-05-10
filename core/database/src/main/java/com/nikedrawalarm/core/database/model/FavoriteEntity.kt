package com.nikedrawalarm.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class FavoriteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val productId: String,
    val favoriteDate: Long
)
