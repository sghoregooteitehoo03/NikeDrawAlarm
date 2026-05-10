package com.nikedrawalarm.core.database.model

import androidx.room.Embedded
import com.nikedrawalarm.core.database.model.FavoriteEntity
import com.nikedrawalarm.core.database.model.ProductEntity

@Entity
data class FavoriteProductEntity(
    @Embedded
    val productEntity: ProductEntity,
    @Embedded
    val favoriteEntity: FavoriteEntity
)