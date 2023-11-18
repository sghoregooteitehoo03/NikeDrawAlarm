package com.nikealarm.nikedrawalarm.data.model.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Relation

@Entity
data class FavoriteProductEntity(
    @Embedded
    val productEntity: ProductEntity,
    @Embedded
    val favoriteEntity: FavoriteEntity
)