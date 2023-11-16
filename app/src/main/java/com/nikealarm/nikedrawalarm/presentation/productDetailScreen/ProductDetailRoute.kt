package com.nikealarm.nikedrawalarm.presentation.productDetailScreen

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nikealarm.nikedrawalarm.data.model.entity.NotificationEntity
import com.nikealarm.nikedrawalarm.domain.model.ProductInfo
import com.nikealarm.nikedrawalarm.presentation.setNotificationScreen.SetNotificationDialog
import com.nikealarm.nikedrawalarm.presentation.ui.DisposableEffectWithLifeCycle

// TODO:
//  . 최근 방문 기능

@Composable
fun ProductDetailRoute(
    viewModel: ProductDetailViewModel = hiltViewModel(),
    sendProductInfo: ProductInfo?,
    isDialogOpen: Boolean,
    onDismiss: () -> Unit,
    onNotificationChange: (NotificationEntity?) -> Unit,
    onDialogButtonClick: () -> Unit,
    onDispose: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    DisposableEffectWithLifeCycle(
        onCreate = {
            viewModel.initValue(sendProductInfo)
        },
        onDispose = onDispose
    )

    ProductDetailScreen(
        state = state,
        onFavoriteClick = viewModel::clickFavorite,
        onLearnMoreClick = { url ->
            openCustomTabs(context, url)
        }
    )

    if (!state.isLoading && state.productInfo!!.eventDate != 0L) {
        onNotificationChange(state.notificationEntity)
    }

    if (isDialogOpen) {
        SetNotificationDialog(
            onDismissRequest = onDismiss,
            onButtonClick = {
                viewModel.setNotification(it)
                onDialogButtonClick()
            },
            settingTime = state.notificationEntity?.notificationDate ?: 0L
        )
    }
}

@Composable
fun LoadProductDetailRoute(
    viewModel: ProductDetailViewModel = hiltViewModel(),
    productId: String,
    isDialogOpen: Boolean,
    onDispose: () -> Unit,
    onDismiss: () -> Unit,
    onNotificationChange: (NotificationEntity?) -> Unit,
    onDialogButtonClick: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    DisposableEffectWithLifeCycle(
        onCreate = {
            viewModel.loadProduct(productId)
        },
        onDispose = onDispose
    )

    ProductDetailScreen(
        state = state,
        onFavoriteClick = viewModel::clickFavorite,
        onLearnMoreClick = { url ->
            openCustomTabs(
                context,
                url
            )
        }
    )

    if (!state.isLoading && state.productInfo!!.eventDate != 0L) {
        onNotificationChange(state.notificationEntity)
    }

    if (isDialogOpen) {
        SetNotificationDialog(
            onDismissRequest = onDismiss,
            onButtonClick = {
                viewModel.setNotification(it)
                onDialogButtonClick()
            },
            settingTime = state.notificationEntity?.notificationDate ?: 0L
        )
    }
}

private fun openCustomTabs(context: Context, url: String) {
    try {
        CustomTabsIntent.Builder()
            .build()
            .launchUrl(context, Uri.parse(url))
    } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(context, "크롬 브라우저가 존재하지 않습니다.", Toast.LENGTH_SHORT)
            .show()
    }
}