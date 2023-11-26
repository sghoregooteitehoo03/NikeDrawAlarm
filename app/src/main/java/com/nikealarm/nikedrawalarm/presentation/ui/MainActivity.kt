package com.nikealarm.nikedrawalarm.presentation.ui

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.preferencesDataStore
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import androidx.work.impl.model.Preference
import com.nikealarm.nikedrawalarm.presentation.collectionDetailScreen.CollectionDetailRoute
import com.nikealarm.nikedrawalarm.presentation.favoriteMoreScreen.FavoriteMoreRoute
import com.nikealarm.nikedrawalarm.presentation.favoriteScreen.FavoriteRoute
import com.nikealarm.nikedrawalarm.presentation.productDetailScreen.LoadProductDetailRoute
import com.nikealarm.nikedrawalarm.presentation.productDetailScreen.ProductDetailRoute
import com.nikealarm.nikedrawalarm.presentation.productScreen.ProductRoute
import com.nikealarm.nikedrawalarm.presentation.settingScreen.SettingRoute
import com.nikealarm.nikedrawalarm.presentation.upcomingScreen.UpcomingRoute
import com.nikealarm.nikedrawalarm.util.Constants
import com.plcoding.cryptocurrencyappyt.presentation.ui.theme.Black
import com.plcoding.cryptocurrencyappyt.presentation.ui.theme.NikeDrawAssistant
import com.plcoding.cryptocurrencyappyt.presentation.ui.theme.Typography
import dagger.hilt.android.AndroidEntryPoint

// TODO: Navigation 관리하는 Class 구현
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val gViewModel by viewModels<GlobalViewModel>()
    private val bottomScreenList = listOf(
        UiScreen.ProductScreen,
        UiScreen.UpcomingScreen,
        UiScreen.FavoriteScreen
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createChannel() // Notification channel 생성
        setContent {
            NikeDrawAssistant {
                val navController = rememberNavController()

                Scaffold(
                    topBar = {
                        val backStack = navController.currentBackStackEntryAsState()
                        val currentRoute = backStack.value?.destination?.route ?: ""

                        TopAppBar(
                            elevation = 0.dp,
                        ) {
                            NikeTopAppBar(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 14.dp, end = 14.dp)
                                    .align(Alignment.CenterVertically),
                                title = when (currentRoute) {
                                    UiScreen.ProductScreen.route,
                                    UiScreen.UpcomingScreen.route,
                                    UiScreen.FavoriteScreen.route,
                                    UiScreen.SettingScreen.route -> {
                                        currentRoute
                                    }

                                    UiScreen.FavoriteMoreScreen.route -> {
                                        gViewModel.getJoinedProductCategory()
                                            ?.text ?: ""
                                    }

                                    else -> {
                                        ""
                                    }
                                },
                                navigationIcon = {
                                    when (currentRoute) {
                                        UiScreen.ProductScreen.route,
                                        UiScreen.UpcomingScreen.route,
                                        UiScreen.FavoriteScreen.route -> {
                                        }

                                        else -> {
                                            Row(modifier = it) {
                                                Icon(
                                                    imageVector = Icons.Default.ArrowBackIos,
                                                    contentDescription = "뒤로가기",
                                                    modifier = Modifier
                                                        .size(24.dp)
                                                        .clickable { navController.navigateUp() },
                                                    tint = Black
                                                )
                                                Spacer(modifier = Modifier.width(14.dp))
                                            }
                                        }
                                    }
                                },
                                actionIcon = {
                                    when (currentRoute) {
                                        UiScreen.ProductScreen.route,
                                        UiScreen.UpcomingScreen.route,
                                        UiScreen.FavoriteScreen.route -> {
                                            Icon(
                                                imageVector = Icons.Default.Settings,
                                                contentDescription = "설정",
                                                tint = Black,
                                                modifier = it
                                                    .size(24.dp)
                                                    .clickable {
                                                        navController.navigate(route = UiScreen.SettingScreen.route)
                                                    },
                                            )
                                        }

                                        UiScreen.ProductDetailScreen.route, UiScreen.LoadProductDetailScreen.route -> {
                                            val notificationEntity by gViewModel.notificationEntity
                                            if (notificationEntity != null) {
                                                val icon =
                                                    if (notificationEntity!!.notificationDate != 0L) {
                                                        Icons.Default.Notifications
                                                    } else {
                                                        Icons.Default.NotificationsNone
                                                    }

                                                Icon(
                                                    imageVector = icon,
                                                    contentDescription = "알림",
                                                    modifier = it
                                                        .size(24.dp)
                                                        .clickable {
                                                            gViewModel.setActionEvent(ActionEvent.ActionNotification)
                                                        },
                                                    tint = Black
                                                )
                                            }
                                        }

                                        else -> {}
                                    }
                                }
                            )
                        }
                    },
                    bottomBar = {
                        val backStack by navController.currentBackStackEntryAsState()
                        val currentDestination = backStack?.destination

                        when (currentDestination?.route ?: "") {
                            UiScreen.ProductScreen.route,
                            UiScreen.UpcomingScreen.route,
                            UiScreen.FavoriteScreen.route -> {
                                BottomNavigation {
                                    bottomScreenList.forEach { screen ->
                                        val selected =
                                            currentDestination?.hierarchy?.any { it.route == screen.route } == true

                                        BottomNavigationItem(
                                            selected = selected,
                                            label = {
                                                Text(
                                                    text = screen.route,
                                                    style = Typography.subtitle1.copy(
                                                        fontWeight = if (selected) {
                                                            FontWeight.Bold
                                                        } else {
                                                            FontWeight.Normal
                                                        }
                                                    )
                                                )
                                            },
                                            onClick = {
                                                navController.navigate(screen.route) {
                                                    popUpTo(navController.graph.findStartDestination().id) {
                                                        saveState = true
                                                    }
                                                    launchSingleTop = true
                                                    restoreState = true
                                                }
                                            },
                                            icon = {
                                                val iconRes = if (selected) {
                                                    screen.bottomSelectedIcon
                                                } else {
                                                    screen.bottomUnSelectedIcon
                                                }

                                                Icon(
                                                    painterResource(id = iconRes),
                                                    screen.route
                                                )
                                            })
                                    }
                                }
                            }

                            else -> {}
                        }
                    }
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = UiScreen.ProductScreen.route,
                        modifier = Modifier.padding(it)
                    ) {
                        composable(route = UiScreen.ProductScreen.route) {
                            ProductRoute(
                                onProductItemClick = { product ->
                                    if (product.collection != null) {
                                        gViewModel.sendProductData(product)
                                        navController.navigate(route = UiScreen.CollectionDetailScreen.route)
                                    } else {
                                        gViewModel.sendProductInfoData(product.productInfoList[0])
                                        navController.navigate(route = UiScreen.ProductDetailScreen.route)
                                    }
                                },
                                onCreate = { gViewModel.clearData() }
                            )
                        }
                        composable(route = UiScreen.UpcomingScreen.route) {
                            // TODO: 나중에 clearData() 추가하기
                            UpcomingRoute(
                                onProductClick = { productInfo ->
                                    gViewModel.sendProductInfoData(productInfo)
                                    navController.navigate(route = UiScreen.ProductDetailScreen.route)
                                }
                            )
                        }
                        composable(route = UiScreen.FavoriteScreen.route) {
                            FavoriteRoute(
                                onProductClick = { productEntity ->
                                    val route =
                                        UiScreenName.LOAD_PRODUCT_DETAIL_SCREEN +
                                                "?id=${productEntity.productId}&slug=${
                                                    productEntity.url.substringAfter("t/")
                                                }"
                                    navController.navigate(route = route)
                                },
                                onMoreClick = { joinedProductCategory ->
                                    gViewModel.sendJoinedProductCategory(joinedProductCategory)
                                    navController.navigate(UiScreen.FavoriteMoreScreen.route)
                                },
                                onCreate = { gViewModel.clearData() }
                            )
                        }
                        composable(route = UiScreen.SettingScreen.route) {
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

                                    if (intent.resolveActivity(applicationContext.packageManager) != null) {
                                        startActivity(intent)
                                    }
                                },
                                onDismiss = { gViewModel.dialogOpen(DialogScreen.DialogDismiss) }
                            )
                        }
                        composable(
                            route = UiScreen.ProductDetailScreen.route
                        ) {
                            val dialogScreen by gViewModel.dialogScreen

                            ProductDetailRoute(
                                sendProductInfo = gViewModel.getProductInfoData(),
                                dialogScreen = dialogScreen,
                                actionEvent = gViewModel.event,
                                openDialog = { gViewModel.dialogOpen(it) },
                                onDismiss = { gViewModel.dialogOpen(DialogScreen.DialogDismiss) },
                                onDialogButtonClick = {
                                    when (dialogScreen) {
                                        DialogScreen.DialogAllowNotify -> {
                                            navController.navigate(route = UiScreen.SettingScreen.route)
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
                                            navController.navigate(route = UiScreen.SettingScreen.route)
                                            gViewModel.dialogOpen(DialogScreen.DialogDismiss)
                                        }

                                        else -> {}
                                    }
                                }
                            )
                        }
                        composable(route = UiScreen.CollectionDetailScreen.route) {
                            CollectionDetailRoute(
                                sendProduct = gViewModel.getProductData(),
                                onProductItemClick = { productInfo ->
                                    gViewModel.sendProductInfoData(productInfo)
                                    navController.navigate(route = UiScreen.ProductDetailScreen.route)
                                },
                                onDispose = { gViewModel.sendProductData(null) }
                            )
                        }
                        composable(route = UiScreen.FavoriteMoreScreen.route) {
                            FavoriteMoreRoute(
                                sendCategory = gViewModel.getJoinedProductCategory(),
                                onProductClick = { productEntity ->
                                    val route =
                                        UiScreenName.LOAD_PRODUCT_DETAIL_SCREEN +
                                                "?id=${productEntity.productId}&slug=${
                                                    productEntity.url.substringAfter("t/")
                                                }"
                                    navController.navigate(route = route)
                                },
                                onDispose = { }
                            )
                        }
                    }
                }
            }
        }
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val productNotify = "제품 출시 알림"
            val drawProductNotify = "Draw 신제품 알림"
            val importance = NotificationManager.IMPORTANCE_DEFAULT

            val channels = listOf(
                NotificationChannel(
                    Constants.CHANNEL_ID_PRODUCT_NOTIFICATION,
                    productNotify,
                    importance
                ),
                NotificationChannel(
                    Constants.CHANNEL_ID_DRAW_NEW_PRODUCT_NOTIFICATION,
                    drawProductNotify,
                    importance
                )
            )

            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannels(channels)
        }
    }
}