package com.nikedrawalarm.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.nikedrawalarm.core.database.dao.ProductDao
import com.nikedrawalarm.core.database.model.FavoriteEntity
import com.nikedrawalarm.core.database.model.LatestEntity
import com.nikedrawalarm.core.database.model.NotificationEntity
import com.nikedrawalarm.core.database.model.ProductEntity

@Database(
    entities = [ProductEntity::class, FavoriteEntity::class, NotificationEntity::class, LatestEntity::class],
    version = 1,
    exportSchema = false
)
internal abstract class ProductDatabase : RoomDatabase() {
    abstract fun getDao(): ProductDao
}