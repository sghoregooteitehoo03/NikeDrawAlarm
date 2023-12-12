package com.nikealarm.nikedrawalarm.presentation.productDetailScreen

import android.app.Notification
import com.nikealarm.nikedrawalarm.data.model.entity.NotificationEntity
import com.nikealarm.nikedrawalarm.domain.model.ProductInfo

sealed interface ProductDetailUiEvent {
    data class NotificationChange(val notificationEntity: NotificationEntity?) : ProductDetailUiEvent
    data object ClickFavorite : ProductDetailUiEvent
    data class ClickLearnMore(val url: String) : ProductDetailUiEvent
    data class SuccessInsertNotification(val notificationTime: Long) : ProductDetailUiEvent
    data class Error(val message: String) : ProductDetailUiEvent
}