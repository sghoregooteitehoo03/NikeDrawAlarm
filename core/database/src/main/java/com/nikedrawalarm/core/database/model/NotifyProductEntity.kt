package com.nikedrawalarm.core.database.model

import androidx.room.Embedded
import com.nikedrawalarm.core.database.model.NotificationEntity
import com.nikedrawalarm.core.database.model.ProductEntity

data class NotifyProductEntity(
    @Embedded
    val productEntity: ProductEntity,
    @Embedded
    val notificationEntity: NotificationEntity
)
