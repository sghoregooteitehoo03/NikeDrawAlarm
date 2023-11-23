package com.nikealarm.nikedrawalarm.presentation.productDetailScreen

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nikealarm.nikedrawalarm.data.model.entity.NotificationEntity
import com.nikealarm.nikedrawalarm.domain.model.ProductInfo
import com.nikealarm.nikedrawalarm.presentation.setNotificationScreen.SetNotificationDialog
import com.nikealarm.nikedrawalarm.presentation.ui.DialogScreen
import com.nikealarm.nikedrawalarm.presentation.ui.DisposableEffectWithLifeCycle

@Composable
fun ProductDetailRoute(
    viewModel: ProductDetailViewModel = hiltViewModel(),
    sendProductInfo: ProductInfo?,
    dialogScreen: DialogScreen,
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
        onNotificationChange = onNotificationChange,
        onFavoriteClick = viewModel::clickFavorite,
        onLearnMoreClick = { url ->
            openCustomTabs(context, url)
        }
    )

    if (!state.isLoading) {
        onNotificationChange(state.notificationEntity)
    }

    when (dialogScreen) {
        DialogScreen.DialogSetNotify -> {
            SetNotificationDialog(
                onDismissRequest = onDismiss,
                onButtonClick = {
                    viewModel.setNotification(it)
                    onDismiss()
                },
                settingTime = state.notificationEntity?.notificationDate ?: 0L
            )
        }

        else -> {}
    }
}

@Composable
fun LoadProductDetailRoute(
    viewModel: ProductDetailViewModel = hiltViewModel(),
    productId: String,
    slug: String,
    dialogScreen: DialogScreen,
    onDismiss: () -> Unit,
    onNotificationChange: (NotificationEntity?) -> Unit,
    onDialogButtonClick: () -> Unit,
    onDispose: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    DisposableEffectWithLifeCycle(
        onCreate = {
            viewModel.loadProduct(productId, slug)
        },
        onDispose = onDispose
    )

    ProductDetailScreen(
        state = state,
        onNotificationChange = onNotificationChange,
        onFavoriteClick = viewModel::clickFavorite,
        onLearnMoreClick = { url ->
            openCustomTabs(
                context,
                url
            )
        }
    )

    when (dialogScreen) {
        DialogScreen.DialogSetNotify -> {
            SetNotificationDialog(
                onDismissRequest = onDismiss,
                onButtonClick = {
                    viewModel.setNotification(it)
                    onDismiss()
                },
                settingTime = state.notificationEntity?.notificationDate ?: 0L
            )
        }

        else -> {}
    }
}

// TODO: 사이트 안열리는 버그
private fun openCustomTabs(context: Context, url: String) {
    try {
        Log.i("check", "url: $url")
        CustomTabsIntent.Builder()
            .build()
            .launchUrl(context, url.toUri())
    } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(context, "크롬 브라우저가 존재하지 않습니다.", Toast.LENGTH_SHORT)
            .show()
    }
}