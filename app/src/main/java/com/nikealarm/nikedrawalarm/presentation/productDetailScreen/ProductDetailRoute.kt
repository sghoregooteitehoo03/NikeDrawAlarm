package com.nikealarm.nikedrawalarm.presentation.productDetailScreen

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nikealarm.nikedrawalarm.R
import com.nikealarm.nikedrawalarm.data.model.entity.NotificationEntity
import com.nikealarm.nikedrawalarm.domain.model.ProductInfo
import com.nikealarm.nikedrawalarm.presentation.setNotificationScreen.SetNotificationDialog
import com.nikealarm.nikedrawalarm.presentation.settingScreen.InformationDialog
import com.nikealarm.nikedrawalarm.presentation.ui.ActionEvent
import com.nikealarm.nikedrawalarm.presentation.ui.DialogScreen
import com.nikealarm.nikedrawalarm.presentation.ui.DisposableEffectWithLifeCycle
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest

@Composable
fun ProductDetailRoute(
    viewModel: ProductDetailViewModel = hiltViewModel(),
    sendProductInfo: ProductInfo?,
    actionEvent: SharedFlow<ActionEvent>,
    dialogScreen: DialogScreen,
    openDialog: (DialogScreen) -> Unit,
    onDismiss: () -> Unit,
    onNotificationChange: (NotificationEntity?) -> Unit,
    onDialogButtonClick: () -> Unit,
    onDispose: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(key1 = actionEvent) {
        actionEvent.collectLatest { event ->
            when (event) {
                is ActionEvent.ActionNotification -> {
                    if (state.isAllowNotify) {
                        openDialog(DialogScreen.DialogSetNotify)
                    } else {
                        openDialog(DialogScreen.DialogAllowNotify)
                    }
                }
            }
        }
    }

    DisposableEffectWithLifeCycle(
        onCreate = {
            viewModel.initValue(
                sendProductInfo,
                onNotificationChange
            )
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

    when (dialogScreen) {
        DialogScreen.DialogSetNotify -> {
            SetNotificationDialog(
                onDismissRequest = onDismiss,
                onButtonClick = {
                    viewModel.setNotification(notificationTime = it)
                    onDismiss()
                },
                settingTime = state.notificationEntity?.notificationDate ?: 0L
            )
        }

        DialogScreen.DialogAllowNotify -> {
            InformationDialog(
                modifier = Modifier.fillMaxWidth(),
                title = "알림 설정",
                explain = stringResource(id = R.string.allow_notification_explain),
                buttonText = "이동",
                onDismissRequest = onDismiss,
                onAllowClick = onDialogButtonClick
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
    actionEvent: SharedFlow<ActionEvent>,
    openDialog: (DialogScreen) -> Unit,
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
            viewModel.loadProduct(
                productId,
                slug,
                onNotificationChange
            )
        },
        onDispose = onDispose
    )

    LaunchedEffect(key1 = actionEvent) {
        actionEvent.collectLatest { event ->
            when (event) {
                is ActionEvent.ActionNotification -> {
                    if (state.isAllowNotify) {
                        openDialog(DialogScreen.DialogSetNotify)
                    } else {
                        openDialog(DialogScreen.DialogAllowNotify)
                    }
                }
            }
        }
    }

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

    when (dialogScreen) {
        DialogScreen.DialogSetNotify -> {
            SetNotificationDialog(
                onDismissRequest = onDismiss,
                onButtonClick = {
                    viewModel.setNotification(notificationTime = it)
                    onDismiss()
                },
                settingTime = state.notificationEntity?.notificationDate ?: 0L
            )
        }

        DialogScreen.DialogAllowNotify -> {
            InformationDialog(
                modifier = Modifier.fillMaxWidth(),
                title = "알림 설정",
                explain = stringResource(id = R.string.allow_notification_explain),
                buttonText = "이동",
                onDismissRequest = onDismiss,
                onAllowClick = onDialogButtonClick
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