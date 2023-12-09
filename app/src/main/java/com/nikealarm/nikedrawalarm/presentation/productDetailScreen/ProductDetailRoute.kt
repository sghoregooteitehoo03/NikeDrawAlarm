package com.nikealarm.nikedrawalarm.presentation.productDetailScreen

import android.content.Context
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
import com.nikealarm.nikedrawalarm.util.Constants
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flattenMerge
import kotlinx.coroutines.flow.flowOf
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalCoroutinesApi::class)
@Composable
fun ProductDetailRoute(
    viewModel: ProductDetailViewModel = hiltViewModel(),
    sendProductInfo: ProductInfo?,
    actionEvent: SharedFlow<ActionEvent>,
    showSnackBar: (String) -> Unit,
    dialogScreen: DialogScreen,
    openDialog: (DialogScreen) -> Unit,
    onDismiss: () -> Unit,
    onNotificationChange: (NotificationEntity?) -> Unit,
    onDialogButtonClick: () -> Unit,
    onDispose: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(key1 = true) {
        flowOf(actionEvent, viewModel.uiEvent)
            .flattenMerge()
            .collectLatest { event ->
                when (event) {
                    is ActionEvent.ActionNotification -> {
                        if (state.isAllowNotify) {
                            openDialog(DialogScreen.DialogSetNotify)
                        } else {
                            openDialog(DialogScreen.DialogAllowNotify)
                        }
                    }

                    is ProductDetailUiEvent.ClickFavorite -> {
                        viewModel.clickFavorite()
                    }

                    is ProductDetailUiEvent.ClickLearnMore -> {
                        openCustomTabs(context, url = event.url)
                    }

                    is ProductDetailUiEvent.SuccessInsertNotification -> {
                        val message = if (event.notificationTime != 0L) {
                            SimpleDateFormat(
                                if (event.notificationTime >= 3600000) {
                                    "제품 출시 h시간 전에 알림이 울립니다."
                                } else {
                                    "제품 출시 m분 전에 알림이 울립니다."
                                }, Locale.KOREA
                            )
                                .format(event.notificationTime.minus(Constants.LOCALIZING))
                        } else {
                            "알림이 해제 되었습니다."
                        }

                        showSnackBar(message)
                    }

                    is ProductDetailUiEvent.Error -> {
                        showSnackBar(event.message)
                    }

                    else -> {}
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
        onEvent = viewModel::handelEvent
    )

    when (dialogScreen) {
        DialogScreen.DialogSetNotify -> {
            SetNotificationDialog(
                onDismissRequest = onDismiss,
                onButtonClick = {
                    viewModel.setNotification(notificationTime = it)
                    onDismiss()
                },
                settingTime = state.notificationEntity?.notificationDate ?: 0L,
                context = context
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

@OptIn(ExperimentalCoroutinesApi::class)
@Composable
fun LoadProductDetailRoute(
    viewModel: ProductDetailViewModel = hiltViewModel(),
    productId: String,
    slug: String,
    actionEvent: SharedFlow<ActionEvent>,
    showSnackBar: (String) -> Unit,
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

    LaunchedEffect(key1 = true) {
        flowOf(actionEvent, viewModel.uiEvent)
            .flattenMerge()
            .collectLatest { event ->
                when (event) {
                    is ActionEvent.ActionNotification -> {
                        if (state.isAllowNotify) {
                            openDialog(DialogScreen.DialogSetNotify)
                        } else {
                            openDialog(DialogScreen.DialogAllowNotify)
                        }
                    }

                    is ProductDetailUiEvent.ClickFavorite -> {
                        viewModel.clickFavorite()
                    }

                    is ProductDetailUiEvent.ClickLearnMore -> {
                        openCustomTabs(context, url = event.url)
                    }

                    is ProductDetailUiEvent.SuccessInsertNotification -> {
                        val message = if (event.notificationTime != 0L) {
                            SimpleDateFormat(
                                if (event.notificationTime >= 3600000) {
                                    "제품 출시 h시간 전에 알림이 울립니다."
                                } else {
                                    "제품 출시 m분 전에 알림이 울립니다."
                                }, Locale.KOREA
                            )
                                .format(event.notificationTime.minus(Constants.LOCALIZING))
                        } else {
                            "알림이 해제 되었습니다."
                        }

                        showSnackBar(message)
                    }
                    is ProductDetailUiEvent.Error -> {
                        showSnackBar(event.message)
                    }
                    else -> {}
                }
            }
    }

    ProductDetailScreen(
        state = state,
        onEvent = viewModel::handelEvent
    )

    when (dialogScreen) {
        DialogScreen.DialogSetNotify -> {
            SetNotificationDialog(
                onDismissRequest = onDismiss,
                onButtonClick = {
                    viewModel.setNotification(notificationTime = it)
                    onDismiss()
                },
                settingTime = state.notificationEntity?.notificationDate ?: 0L,
                context = context
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

private fun openCustomTabs(context: Context, url: String) {
    try {
        CustomTabsIntent.Builder()
            .build()
            .launchUrl(context, url.toUri())
    } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(context, "크롬 브라우저가 존재하지 않습니다.", Toast.LENGTH_SHORT)
            .show()
    }
}