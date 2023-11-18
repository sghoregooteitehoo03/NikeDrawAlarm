package com.nikealarm.nikedrawalarm.data.model.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Relation

@Entity
data class LatestProductEntity(
    @Embedded
    val productEntity: ProductEntity,
    @Embedded
    val latestEntity: LatestEntity
)
