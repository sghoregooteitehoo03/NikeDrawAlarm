package com.nikealarm.core.domain.repository

import androidx.paging.PagingData
import com.nikealarm.core.common.Result
import com.nikealarm.core.model.Favorite
import com.nikealarm.core.model.JoinedProduct
import com.nikealarm.core.model.JoinedProductType
import com.nikealarm.core.model.Latest
import com.nikealarm.core.model.Notification
import com.nikealarm.core.model.ProductInfo
import kotlinx.coroutines.flow.Flow

interface DatabaseRepository {
    fun getPagingJoinedProduct(joinedCategory: JoinedProductType): Flow<PagingData<JoinedProduct>>

    suspend fun getProductData(productId: String): Result<ProductInfo>

    fun getFavoriteData(productId: String): Flow<Favorite?>

    fun getNotificationData(productId: String): Flow<Notification?>

    suspend fun getNotificationsData(): Result<List<Notification>>

    fun getLatestProductsData(limit: Int): Flow<List<Pair<ProductInfo, Latest>>>

    suspend fun getNotifyProductsData(): Result<List<Pair<ProductInfo, Latest>>>

    fun getNotifyProductsData(limit: Int): Flow<List<Pair<ProductInfo, Notification>>>

    fun getFavoriteProductsData(limit: Int): Flow<List<Pair<ProductInfo, Favorite>>>

    suspend fun insertProductData(productInfo: ProductInfo): Result<Unit>

    suspend fun insertLatestData(productInfo: ProductInfo): Result<Unit>

    suspend fun insertNotificationData(
        productInfo: ProductInfo,
        triggerTime: Long,
        notificationTime: Long
    ): Result<Unit>

    suspend fun insertFavoriteData(productInfo: ProductInfo): Result<Unit>

    suspend fun deleteFavoriteData(productId: String): Result<Unit>

    suspend fun deleteNotificationData(productId: String): Result<Unit>

    suspend fun clearLatestData(): Result<Unit>

    suspend fun clearNotificationData(): Result<Unit>

    suspend fun clearFavoriteData(): Result<Unit>

    fun getAllowNotification(): Flow<Boolean>

    fun getAllowDrawNotification(): Flow<Boolean>

    suspend fun setAllowNotification(isAllow: Boolean): Result<Unit>

    suspend fun setAllowDrawNotification(isAllow: Boolean): Result<Unit>
}