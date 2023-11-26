package com.nikealarm.nikedrawalarm.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.nikealarm.nikedrawalarm.data.model.entity.FavoriteEntity
import com.nikealarm.nikedrawalarm.data.model.entity.LatestEntity
import com.nikealarm.nikedrawalarm.data.model.entity.NotificationEntity
import com.nikealarm.nikedrawalarm.data.repository.dataSource.JoinedProductPagingSource
import com.nikealarm.nikedrawalarm.data.repository.database.ProductDao
import com.nikealarm.nikedrawalarm.domain.model.JoinedProductCategory
import com.nikealarm.nikedrawalarm.domain.model.ProductInfo
import com.nikealarm.nikedrawalarm.util.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ProductDatabaseRepository @Inject constructor(
    private val settingDataStore: DataStore<Preferences>,
    private val dao: ProductDao
) {
    fun getPagingJoinedProduct(joinedCategory: JoinedProductCategory) = Pager(
        config = PagingConfig(20)
    ) {
        JoinedProductPagingSource(dao, joinedCategory)
    }.flow

    suspend fun getProductData(productId: String) =
        dao.getProductData(productId)

    fun getFavoriteData(productId: String) =
        dao.getFavoriteData(productId)

    fun getNotificationData(productId: String) =
        dao.getNotificationData(productId = productId)

    suspend fun getNotificationsData() =
        dao.getNotificationsData()

    fun getLatestProductsData(limit: Int) =
        dao.getLatestProductsData(limit)

    fun getNotifyProductsData(limit: Int) =
        dao.getNotifyProductsData(limit)

    fun getFavoriteProductsData(limit: Int) =
        dao.getFavoriteProductsData(limit)

    suspend fun insertProductData(productInfo: ProductInfo) {
        val productEntity = productInfo.getProductEntity()

        dao.insertProductData(productEntity)
    }

    suspend fun insertLatestData(productInfo: ProductInfo) {
        val latestEntity =
            LatestEntity(productId = productInfo.productId, latestDate = System.currentTimeMillis())

        insertProductData(productInfo)
        dao.insertLatestData(latestEntity)
    }

    suspend fun insertNotificationData(
        productInfo: ProductInfo,
        triggerTime: Long,
        notificationTime: Long
    ) {
        val notificationEntity = NotificationEntity(
            productId = productInfo.productId,
            triggerTime = triggerTime,
            notificationDate = notificationTime,
            addedDate = System.currentTimeMillis()
        )

        insertProductData(productInfo)
        dao.insertNotificationData(notificationEntity)
    }

    suspend fun insertFavoriteData(productInfo: ProductInfo) {
        val favoriteEntity = FavoriteEntity(
            productId = productInfo.productId,
            favoriteDate = System.currentTimeMillis()
        )

        insertProductData(productInfo)
        dao.insertFavoriteData(favoriteEntity)
    }


    suspend fun deleteFavoriteData(productId: String) {
        dao.deleteFavoriteData(productId)
    }

    suspend fun deleteNotificationData(productId: String) {
        dao.deleteNotificationData(productId)
    }

    suspend fun clearLatestData() {
        dao.clearLatestData()
    }

    suspend fun clearNotificationData() {
        dao.clearNotificationData()
    }

    suspend fun clearFavoriteData() {
        dao.clearFavoriteData()
    }

    fun getAllowNotification(): Flow<Boolean> = settingDataStore.data
        .map { preference ->
            preference[Constants.DATA_KEY_ALLOW_NOTIFICATION] ?: false
        }

    fun getAllowDrawNotification(): Flow<Boolean> = settingDataStore.data
        .map { preference ->
            preference[Constants.DATA_KEY_ALLOW_DRAW_NOTIFICATION] ?: false
        }

    suspend fun setAllowNotification(isAllow: Boolean) {
        settingDataStore.edit { settings ->
            settings[Constants.DATA_KEY_ALLOW_NOTIFICATION] = isAllow
        }
    }

    suspend fun setAllowDrawNotification(isAllow: Boolean) {
        settingDataStore.edit { settings ->
            settings[Constants.DATA_KEY_ALLOW_DRAW_NOTIFICATION] = isAllow
        }
    }
}