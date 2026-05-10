package com.nikedrawalarm.core.database.model

import androidx.room.Embedded
import com.nikedrawalarm.core.database.model.LatestEntity
import com.nikedrawalarm.core.database.model.ProductEntity

@Entity
data class LatestProductEntity(
    @Embedded
    val productEntity: ProductEntity,
    @Embedded
    val latestEntity: LatestEntity
)
