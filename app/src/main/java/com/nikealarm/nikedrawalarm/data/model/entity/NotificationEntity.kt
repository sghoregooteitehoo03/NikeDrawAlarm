package com.nikealarm.nikedrawalarm.data.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class NotificationEntity(
    @PrimaryKey
    val id: Int? = null,
    val productId: String,
    val notificationDate: Long,
    val addedDate: Long
)
