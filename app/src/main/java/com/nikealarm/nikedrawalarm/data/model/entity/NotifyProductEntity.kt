package com.nikealarm.nikedrawalarm.data.model.entity

import androidx.room.Embedded
import androidx.room.Relation

data class NotifyProductEntity(
    @Embedded
    val productEntity: ProductEntity,
    @Embedded
    val notificationEntity: NotificationEntity
)
