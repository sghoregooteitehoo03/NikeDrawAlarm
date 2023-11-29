package com.nikealarm.nikedrawalarm.data.repository.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nikealarm.nikedrawalarm.data.model.entity.FavoriteEntity
import com.nikealarm.nikedrawalarm.data.model.entity.FavoriteProductEntity
import com.nikealarm.nikedrawalarm.data.model.entity.LatestEntity
import com.nikealarm.nikedrawalarm.data.model.entity.LatestProductEntity
import com.nikealarm.nikedrawalarm.data.model.entity.NotificationEntity
import com.nikealarm.nikedrawalarm.data.model.entity.NotifyProductEntity
import com.nikealarm.nikedrawalarm.data.model.entity.ProductEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {

    @Query("SELECT * FROM ProductEntity WHERE :productId = Id")
    suspend fun getProductData(productId: String): ProductEntity?

    @Query("SELECT * FROM FavoriteEntity WHERE :productId = productId")
    fun getFavoriteData(productId: String): Flow<FavoriteEntity?>

    @Query("SELECT * FROM NotificationEntity WHERE :productId = productId")
    fun getNotificationData(productId: String): Flow<NotificationEntity?>

    @Query("SELECT * FROM NotificationEntity")
    suspend fun getNotificationsData(): List<NotificationEntity>

    @Query(
        "SELECT * " +
                "FROM ProductEntity AS product " +
                "INNER JOIN LatestEntity AS latest " +
                "ON product.Id == latest.productId " +
                "ORDER BY latestDate DESC " +
                "LIMIT :limit"
    )
    fun getLatestProductsData(limit: Int): Flow<List<LatestProductEntity>>

    @Query(
        "SELECT * " +
                "FROM ProductEntity AS product " +
                "INNER JOIN LatestEntity AS latest " +
                "ON product.Id == latest.productId " +
                "ORDER BY latestDate DESC " +
                "LIMIT :limit " +
                "OFFSET :offset"
    )
    suspend fun getLatestProductsPageData(limit: Int, offset: Int): List<LatestProductEntity>

    @Query(
        "SELECT * " +
                "FROM ProductEntity AS product " +
                "INNER JOIN NotificationEntity AS notification " +
                "ON product.Id == notification.productId " +
                "ORDER BY product.eventDate ASC "
    )
    fun getNotifyProductsData(): Flow<List<NotifyProductEntity>>

    @Query(
        "SELECT * " +
                "FROM ProductEntity AS product " +
                "INNER JOIN NotificationEntity AS notification " +
                "ON product.Id == notification.productId " +
                "ORDER BY product.eventDate ASC " +
                "LIMIT :limit"
    )
    fun getNotifyProductsData(limit: Int): Flow<List<NotifyProductEntity>>

    @Query(
        "SELECT * " +
                "FROM ProductEntity AS product " +
                "INNER JOIN NotificationEntity AS notification " +
                "ON product.Id == notification.productId " +
                "ORDER BY product.eventDate ASC " +
                "LIMIT :limit " +
                "OFFSET :offset"
    )
    suspend fun getNotifyProductsPageData(limit: Int, offset: Int): List<NotifyProductEntity>

    @Query(
        "SELECT * " +
                "FROM ProductEntity AS product " +
                "INNER JOIN FavoriteEntity AS favorite " +
                "ON product.Id == favorite.productId " +
                "ORDER BY favorite.favoriteDate DESC " +
                "LIMIT :limit"
    )
    fun getFavoriteProductsData(limit: Int): Flow<List<FavoriteProductEntity>>

    @Query(
        "SELECT * " +
                "FROM ProductEntity AS product " +
                "INNER JOIN FavoriteEntity AS favorite " +
                "ON product.Id == favorite.productId " +
                "ORDER BY favorite.favoriteDate DESC " +
                "LIMIT :limit " +
                "OFFSET :offset"
    )
    suspend fun getFavoriteProductsPageData(limit: Int, offset: Int): List<FavoriteProductEntity>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProductData(product: ProductEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLatestData(latest: LatestEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFavoriteData(favorite: FavoriteEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotificationData(notificate: NotificationEntity)


    @Query("DELETE FROM NotificationEntity WHERE :productId = productId")
    suspend fun deleteNotificationData(productId: String)

    @Query("DELETE FROM FavoriteEntity WHERE :productId = productId")
    suspend fun deleteFavoriteData(productId: String)

    @Query("DELETE FROM LatestEntity")
    suspend fun clearLatestData()

    @Query("DELETE FROM NotificationEntity")
    suspend fun clearNotificationData()

    @Query("DELETE FROM FavoriteEntity")
    suspend fun clearFavoriteData()
}