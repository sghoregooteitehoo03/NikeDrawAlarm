package com.nikealarm.nikedrawalarm.data.repository.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.nikealarm.nikedrawalarm.data.model.entity.NotificationEntity
import com.nikealarm.nikedrawalarm.data.model.entity.FavoriteEntity
import com.nikealarm.nikedrawalarm.data.model.entity.LatestEntity
import com.nikealarm.nikedrawalarm.data.model.entity.ProductEntity

@Database(
    entities = [ProductEntity::class, FavoriteEntity::class, NotificationEntity::class, LatestEntity::class],
    version = 1,
    exportSchema = false
)
abstract class ProductDatabase : RoomDatabase() {
    abstract fun getDao(): ProductDao
}