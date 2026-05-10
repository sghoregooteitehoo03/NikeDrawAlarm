package com.nikedrawalarm.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class LatestEntity(
    @PrimaryKey
    val productId: String,
    val latestDate: Long
)
