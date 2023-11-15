package com.nikealarm.nikedrawalarm.data.repository.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nikealarm.nikedrawalarm.data.model.entity.FavoriteEntity
import com.nikealarm.nikedrawalarm.data.model.entity.NotificationEntity
import com.nikealarm.nikedrawalarm.data.model.entity.ProductEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {

    @Query("SELECT * FROM ProductEntity WHERE :productId = productId")
    suspend fun getProductData(productId: String): ProductEntity?

    @Query("SELECT * FROM FavoriteEntity WHERE :productId = productId")
    fun getFavoriteData(productId: String): Flow<FavoriteEntity?>

    @Query("SELECT * FROM NotificationEntity WHERE :productId = productId")
    fun getNotificationData(productId: String): Flow<NotificationEntity?>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertProductData(product: ProductEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFavoriteData(favorite: FavoriteEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotificationData(notificate: NotificationEntity)

    @Query("DELETE FROM NotificationEntity WHERE :productId = productId")
    suspend fun deleteNotificationData(productId: String)

    @Query("DELETE FROM FavoriteEntity WHERE :productId = productId")
    suspend fun deleteFavoriteData(productId: String)
}