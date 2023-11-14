package com.nikealarm.nikedrawalarm.presentation.productDetailScreen

import com.nikealarm.nikedrawalarm.data.model.entity.NotificationEntity
import com.nikealarm.nikedrawalarm.domain.model.ProductInfo

data class ProductDetailUiState(
    val productInfo: ProductInfo? = null,
    val isFavorite: Boolean = false,
    val notificationEntity: NotificationEntity? = null
)