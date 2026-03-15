package com.nikealarm.core.model

data class Notification(
    val productId: String,
    val triggerTime: Long,
    val notificationDate: Long,
    val addedDate: Long
)
