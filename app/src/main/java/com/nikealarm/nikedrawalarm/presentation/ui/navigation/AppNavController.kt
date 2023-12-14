package com.nikealarm.nikedrawalarm.presentation.ui.navigation

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FiniteAnimationSpec
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
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
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
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

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
            navController = navController,
            startDestination = UiScreen.ProductScreen.route,
            enterTransition = { enterTransition() },
            exitTransition = { exitTransition() },
            popEnterTransition = { popEnterTransition() },
            popExitTransition = { popExitTransition() }
        ) {
            composable(
                route = UiScreen.ProductScreen.route,
                enterTransition = { fadeIn(tween(durationMillis = 100)) },
                exitTransition = { fadeOut(tween(durationMillis = 100)) }
            ) {
                ProductRoute(
                    modifier = modifier,
                    actionEvent = gViewModel.event,
                    navigateDetailScreen = { product ->
                        if (product.collection != null) {
                            navigateToCollectionDetailScreen(product)
                        } else {
                            navigateToProductDetailScreen(product.productInfoList[0])
                        }
                    },
                    onCreate = { gViewModel.setNotificationEntity(null) }
                )
            }
            composable(
                route = UiScreen.UpcomingScreen.route,
                enterTransition = { fadeIn(tween(durationMillis = 100)) },
                exitTransition = { fadeOut(tween(durationMillis = 100)) }
            ) {
                UpcomingRoute(
                    modifier = modifier,
                    onProductClick = ::navigateToProductDetailScreen,
                    onCreate = { gViewModel.setNotificationEntity(null) }
                )
            }
            composable(
                route = UiScreen.FavoriteScreen.route,
                enterTransition = { fadeIn(tween(durationMillis = 100)) },
                exitTransition = { fadeOut(tween(durationMillis = 100)) }
            ) {
                FavoriteRoute(
                    modifier = modifier,
                    onProductClick = ::navigateToLoadProductDetailScreen,
                    onMoreClick = ::navigateToFavoriteMoreScreen,
                    onCreate = { gViewModel.setNotificationEntity(null) }
                )
            }
            composable(
                route = UiScreen.SettingScreen.route
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
                arguments = listOf(
                    navArgument("productInfo") { type = NavType.StringType }
                )
            ) {
                val dialogScreen by gViewModel.dialogScreen

                ProductDetailRoute(
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
                    onNotificationChange = { gViewModel.setNotificationEntity(it) }
                )
            }
            composable(
                route = UiScreen.LoadProductDetailScreen.route,
                deepLinks = listOf(navDeepLink {
                    uriPattern =
                        Constants.PRODUCT_DETAIL_URI + "/{productId}/{productSlug}"
                })
            ) { backStackEntry ->
                val dialogScreen by gViewModel.dialogScreen

                LoadProductDetailRoute(
                    actionEvent = gViewModel.event,
                    showSnackBar = { gViewModel.setActionEvent(ActionEvent.ActionShowMessage(it)) },
                    dialogScreen = dialogScreen,
                    openDialog = { gViewModel.dialogOpen(it) },
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
                arguments = listOf(
                    navArgument("product") { type = NavType.StringType }
                )
            ) {
                CollectionDetailRoute(
                    onProductItemClick = { productInfo ->
                        navigateToProductDetailScreen(productInfo)
                    },
                    onCreate = { gViewModel.setNotificationEntity(null) }
                )
            }
            composable(
                route = UiScreen.FavoriteMoreScreen.route
            ) {
                FavoriteMoreRoute(
                    onProductClick = { productEntity ->
                        navigateToLoadProductDetailScreen(productEntity)
                    }
                )
            }
        }
    }

    fun navigateToCollectionDetailScreen(product: Product) {
        val productJson = Json.encodeToString(Product.serializer(), product)
        val route = UiScreenName.COLLECTION_DETAIL_SCREEN + "?product=${productJson}"

        navController.navigate(route = route)
    }

    fun navigateToProductDetailScreen(productInfo: ProductInfo) {
        val updatedProductInfo =
            if (productInfo.eventDate != 0L && productInfo.eventDate < System.currentTimeMillis()) {
                productInfo.copy(eventDate = 0L)
            } else {
                productInfo
            }

        val productInfoJson = Json.encodeToString(ProductInfo.serializer(), updatedProductInfo)
        val route = UiScreenName.PRODUCT_DETAIL_SCREEN + "?productInfo=${productInfoJson}"
        navController.navigate(route = route)
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
        val typeJson = Json.encodeToString(JoinedProductType.serializer(), joinedProductType)
        val route = UiScreenName.FAVORITE_MORE_SCREEN + "?type=${typeJson}"

        navController.navigate(route)
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