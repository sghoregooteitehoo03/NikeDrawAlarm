package com.nikealarm.nikedrawalarm.data.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class NotificationEntity(
    @PrimaryKey
    val productId: String,
    val triggerTime: Long,
    val notificationDate: Long,
    val addedDate: Long
)
