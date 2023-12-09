package com.nikealarm.nikedrawalarm.presentation.ui.navigation

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.nikealarm.nikedrawalarm.data.model.entity.ProductEntity
import com.nikealarm.nikedrawalarm.domain.model.JoinedProductType
import com.nikealarm.nikedrawalarm.domain.model.Product
import com.nikealarm.nikedrawalarm.domain.model.ProductInfo
import com.nikealarm.nikedrawalarm.presentation.collectionDetailScreen.CollectionDetailRoute
import com.nikealarm.nikedrawalarm.presentation.favoriteMoreScreen.FavoriteMoreRoute
import com.nikealarm.nikedrawalarm.presentation.favoriteScreen.FavoriteRoute
import com.nikealarm.nikedrawalarm.presentation.productDetailScreen.LoadProductDetailRoute
import com.nikealarm.nikedrawalarm.presentation.productDetailScreen.ProductDetailRoute
import com.nikealarm.nikedrawalarm.presentation.productScreen.ProductRoute
import com.nikealarm.nikedrawalarm.presentation.settingScreen.SettingRoute
import com.nikealarm.nikedrawalarm.presentation.ui.ActionEvent
import com.nikealarm.nikedrawalarm.presentation.ui.DialogScreen
import com.nikealarm.nikedrawalarm.presentation.ui.GlobalViewModel
import com.nikealarm.nikedrawalarm.presentation.ui.UiScreen
import com.nikealarm.nikedrawalarm.presentation.ui.UiScreenName
import com.nikealarm.nikedrawalarm.presentation.upcomingScreen.UpcomingRoute
import com.nikealarm.nikedrawalarm.util.Constants

// TODO: 화면 전환 부드럽게 바꾸기
class AppNavController(
    private val gViewModel: GlobalViewModel,
    private val navController: NavHostController
) {

    @Composable
    fun AppNavHost(
        modifier: Modifier = Modifier
    ) {
        val context = LocalContext.current

        NavHost(
            modifier = modifier,
            navController = navController,
            startDestination = UiScreen.ProductScreen.route,
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None }
        ) {
            composable(route = UiScreen.ProductScreen.route) {
                ProductRoute(
                    actionEvent = gViewModel.event,
                    navigateDetailScreen = { product ->
                        if (product.collection != null) {
                            navigateToCollectionDetailScreen(product)
                        } else {
                            navigateToProductDetailScreen(product.productInfoList[0])
                        }
                    },
                    onCreate = { gViewModel.clearData() }
                )
            }
            composable(route = UiScreen.UpcomingScreen.route) {
                UpcomingRoute(
                    onProductClick = ::navigateToProductDetailScreen,
                    onCreate = { gViewModel.clearData() }
                )
            }
            composable(route = UiScreen.FavoriteScreen.route) {
                FavoriteRoute(
                    onProductClick = ::navigateToLoadProductDetailScreen,
                    onMoreClick = ::navigateToFavoriteMoreScreen,
                    onCreate = { gViewModel.clearData() }
                )
            }
            composable(
                route = UiScreen.SettingScreen.route,
                enterTransition = { enterTransition() },
                exitTransition = { exitTransition() },
                popEnterTransition = { popEnterTransition() },
                popExitTransition = {
                    popExitTransition()
                }
            ) {
                val dialogScreen by gViewModel.dialogScreen

                SettingRoute(
                    dialogScreen = dialogScreen,
                    openDialog = { _dialogScreen ->
                        gViewModel.dialogOpen(_dialogScreen)
                    },
                    onContactEmailClick = {
                        // 이메일로 바로 이동
                        val intent = Intent(Intent.ACTION_SENDTO).apply {
                            val email = arrayOf(Constants.DEVELOPER_EMAIL)
                            data = Uri.parse("mailto:")
                            putExtra(Intent.EXTRA_EMAIL, email)
                        }

                        if (intent.resolveActivity(context.packageManager) != null) {
                            context.startActivity(intent)
                        }
                    },
                    onDismiss = { gViewModel.dialogOpen(DialogScreen.DialogDismiss) }
                )
            }
            composable(
                route = UiScreen.ProductDetailScreen.route,
                enterTransition = {
                    enterTransition()
                },
                popEnterTransition = {
                    popEnterTransition()
                },
                exitTransition = {
                    exitTransition()
                },
                popExitTransition = {
                    popExitTransition()
                }
            ) {
                val dialogScreen by gViewModel.dialogScreen

                ProductDetailRoute(
                    sendProductInfo = gViewModel.getProductInfoData(),
                    dialogScreen = dialogScreen,
                    actionEvent = gViewModel.event,
                    showSnackBar = { gViewModel.setActionEvent(ActionEvent.ActionShowMessage(it)) },
                    openDialog = { gViewModel.dialogOpen(it) },
                    onDismiss = { gViewModel.dialogOpen(DialogScreen.DialogDismiss) },
                    onDialogButtonClick = {
                        when (dialogScreen) {
                            DialogScreen.DialogAllowNotify -> {
                                navigateToSettingScreen()
                                gViewModel.dialogOpen(DialogScreen.DialogDismiss)
                            }

                            else -> {}
                        }
                    },
                    onNotificationChange = { gViewModel.setNotificationEntity(it) },
                    onDispose = { gViewModel.sendProductInfoData(null) }
                )
            }
            composable(
                route = UiScreen.LoadProductDetailScreen.route,
                enterTransition = {
                    enterTransition()
                },
                popEnterTransition = {
                    popEnterTransition()
                },
                exitTransition = {
                    exitTransition()
                },
                popExitTransition = {
                    popExitTransition()
                },
                deepLinks = listOf(navDeepLink {
                    uriPattern =
                        Constants.PRODUCT_DETAIL_URI + "/{productId}/{productSlug}"
                })
            ) { backStackEntry ->
                val dialogScreen by gViewModel.dialogScreen

                LoadProductDetailRoute(
                    productId = backStackEntry.arguments?.getString("productId") ?: "",
                    slug = backStackEntry.arguments?.getString("productSlug")
                        ?: "",
                    actionEvent = gViewModel.event,
                    showSnackBar = { gViewModel.setActionEvent(ActionEvent.ActionShowMessage(it)) },
                    dialogScreen = dialogScreen,
                    openDialog = { gViewModel.dialogOpen(it) },
                    onDispose = { gViewModel.sendProductInfoData(null) },
                    onDismiss = { gViewModel.dialogOpen(DialogScreen.DialogDismiss) },
                    onNotificationChange = { notification ->
                        gViewModel.setNotificationEntity(notification)
                    },
                    onDialogButtonClick = {
                        when (dialogScreen) {
                            DialogScreen.DialogAllowNotify -> {
                                navigateToSettingScreen()
                                gViewModel.dialogOpen(DialogScreen.DialogDismiss)
                            }

                            else -> {}
                        }
                    }
                )
            }
            composable(
                route = UiScreen.CollectionDetailScreen.route,
                enterTransition = {
                    enterTransition()
                },
                popEnterTransition = {
                    popEnterTransition()
                },
                exitTransition = {
                    exitTransition()
                },
                popExitTransition = {
                    popExitTransition()
                }
            ) {
                CollectionDetailRoute(
                    sendProduct = gViewModel.getProductData(),
                    onProductItemClick = { productInfo ->
                        navigateToProductDetailScreen(productInfo)
                    },
                    onDispose = { gViewModel.sendProductData(null) }
                )
            }
            composable(
                route = UiScreen.FavoriteMoreScreen.route,
                enterTransition = {
                    enterTransition()
                },
                popEnterTransition = {
                    popEnterTransition()
                },
                exitTransition = {
                    exitTransition()
                },
                popExitTransition = {
                    popExitTransition()
                }
            ) {
                FavoriteMoreRoute(
                    sendCategory = gViewModel.getJoinedProductCategory(),
                    onProductClick = { productEntity ->
                        navigateToLoadProductDetailScreen(productEntity)
                    },
                    onDispose = { }
                )
            }
        }
    }

    fun navigateToCollectionDetailScreen(product: Product) {
        gViewModel.sendProductData(product)
        navController.navigate(route = UiScreen.CollectionDetailScreen.route)
    }

    fun navigateToProductDetailScreen(productInfo: ProductInfo) {
        val updatedProductInfo =
            if (productInfo.eventDate != 0L && productInfo.eventDate < System.currentTimeMillis()) {
                productInfo.copy(eventDate = 0L)
            } else {
                productInfo
            }

        gViewModel.sendProductInfoData(updatedProductInfo)
        navController.navigate(route = UiScreen.ProductDetailScreen.route)
    }

    fun navigateToLoadProductDetailScreen(productEntity: ProductEntity) {
        val route =
            UiScreenName.LOAD_PRODUCT_DETAIL_SCREEN +
                    "?id=${productEntity.productId}&slug=${
                        productEntity.url.substringAfter("t/")
                    }"
        navController.navigate(route = route)
    }

    fun navigateToFavoriteMoreScreen(joinedProductType: JoinedProductType) {
        gViewModel.sendJoinedProductCategory(joinedProductType)
        navController.navigate(UiScreen.FavoriteMoreScreen.route)
    }

    fun navigateToSettingScreen() {
        navController.navigate(route = UiScreen.SettingScreen.route)
    }

    private fun enterTransition() =
        fadeIn(animationSpec = tween(500)) + scaleIn(
            animationSpec = tween(300),
            initialScale = 1.2f
        )

    private fun popEnterTransition() =
        fadeIn(animationSpec = tween(500)) + scaleIn(
            animationSpec = tween(300),
            initialScale = 0.8f
        )

    private fun exitTransition() =
        fadeOut(animationSpec = tween(100)) + scaleOut(animationSpec = tween(500))

    private fun popExitTransition() =
        fadeOut(animationSpec = tween(100)) + scaleOut(
            animationSpec = tween(500),
            targetScale = 1.2f
        )

}