package com.nikealarm.core.domain.repository

import androidx.paging.PagingData
import com.nikealarm.core.common.Result
import com.nikealarm.core.model.Product
import com.nikealarm.core.model.ProductInfo
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    fun getPagingProducts(isUpcoming: Boolean = false): Flow<PagingData<Product>>

    fun getPagingUpcoming(): Flow<PagingData<ProductInfo>>

    suspend fun getProductInfo(productId: String, slug: String): Result<ProductInfo>

    fun setNotificationProduct(
        productId: String,
        productUrl: String,
        triggerTime: Long
    ): Result<Unit>

    fun setRepeatNewDrawNotify(): Result<Unit>

    fun isExistProductAlarm(productId: String): Result<Boolean>

    fun isExistRepeatAlarm(): Result<Boolean>

    fun cancelNotificationProduct(productId: String): Result<Unit>

    fun cancelRepeatNewDrawNotify(): Result<Unit>

    fun checkAlarmPermissions(): Result<Boolean>
}