package com.nikealarm.nikedrawalarm.presentation.ui

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
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
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import com.nikealarm.nikedrawalarm.presentation.collectionDetailScreen.CollectionDetailRoute
import com.nikealarm.nikedrawalarm.presentation.favoriteScreen.FavoriteRoute
import com.nikealarm.nikedrawalarm.presentation.productDetailScreen.LoadProductDetailRoute
import com.nikealarm.nikedrawalarm.presentation.productDetailScreen.ProductDetailRoute
import com.nikealarm.nikedrawalarm.presentation.productScreen.ProductRoute
import com.nikealarm.nikedrawalarm.presentation.upcomingScreen.UpcomingRoute
import com.nikealarm.nikedrawalarm.util.Constants
import com.plcoding.cryptocurrencyappyt.presentation.ui.theme.Black
import com.plcoding.cryptocurrencyappyt.presentation.ui.theme.NikeDrawAssistant
import com.plcoding.cryptocurrencyappyt.presentation.ui.theme.Typography
import dagger.hilt.android.AndroidEntryPoint

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
                                    UiScreen.CollectionDetailScreen.route,
                                    UiScreen.ProductDetailScreen.route,
                                    UiScreen.LoadProductDetailScreen.route -> {
                                        ""
                                    }

                                    else -> {
                                        currentRoute
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
                                                modifier = it.size(24.dp),
                                                tint = Black
                                            )
                                        }

                                        UiScreen.ProductDetailScreen.route, UiScreen.LoadProductDetailScreen.route -> {
                                            if (gViewModel.getProductInfoData()?.eventDate != 0L) {
                                                val notificationEntity by remember { gViewModel.notificationEntity }
                                                val icon =
                                                    if (notificationEntity != null) {
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
                                                            // TODO: 설정 화면 구현 후 수정
                                                            gViewModel.dialogOpen(true)
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
                                onCreate = {
                                    gViewModel.sendProductData(null)
                                    gViewModel.setNotificationEntity(null)
                                }
                            )
                        }
                        composable(route = UiScreen.UpcomingScreen.route) {
                            UpcomingRoute(
                                onProductClick = { productInfo ->
                                    gViewModel.sendProductInfoData(productInfo)
                                    navController.navigate(route = UiScreen.ProductDetailScreen.route)
                                }
                            )
                        }
                        composable(route = UiScreen.FavoriteScreen.route) {
                            FavoriteRoute()
                        }
                        composable(
                            route = UiScreen.ProductDetailScreen.route
                        ) { backStack ->
                            val isDialogOpen by gViewModel.isDialogOpen
                            ProductDetailRoute(
                                sendProductInfo = gViewModel.getProductInfoData(),
                                isDialogOpen = isDialogOpen,
                                onDismiss = { gViewModel.dialogOpen(false) },
                                onDialogButtonClick = { gViewModel.dialogOpen(false) },
                                onNotificationChange = { gViewModel.setNotificationEntity(it) },
                                onDispose = { gViewModel.sendProductInfoData(null) }
                            )
                        }
                        composable(
                            route = UiScreen.LoadProductDetailScreen.route,
                            deepLinks = listOf(navDeepLink {
                                uriPattern = Constants.PRODUCT_DETAIL_URI + "/{productId}"
                            })
                        ) { backStackEntry ->
                            val isDialogOpen by gViewModel.isDialogOpen
                            LoadProductDetailRoute(
                                productId = backStackEntry.arguments?.getString("productId") ?: "",
                                isDialogOpen = isDialogOpen,
                                onDispose = { },
                                onDismiss = { gViewModel.dialogOpen(false) },
                                onNotificationChange = { gViewModel.setNotificationEntity(it) },
                                onDialogButtonClick = { gViewModel.dialogOpen(false) }
                            )
                        }
                        composable(route = UiScreen.CollectionDetailScreen.route) {
                            CollectionDetailRoute(
                                sendProduct = gViewModel.getProductData(),
                                onProductItemClick = { productInfo ->
                                    gViewModel.sendProductInfoData(productInfo)
                                    navController.navigate(route = UiScreen.ProductDetailScreen.route)
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
            val name = "제품 출신 알림"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel =
                NotificationChannel(Constants.CHANNEL_ID_PRODUCT_NOTIFICATION, name, importance)

            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}

//                                                            // 푸쉬 알림을 보낼 수 있는 경우
//                                                            if (NotificationManagerCompat
//                                                                    .from(this@MainActivity)
//                                                                    .areNotificationsEnabled()
//                                                            ) {
//                                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//                                                                    // 안드로이드 버전이 14인 경우 권한이 설정되어있는지 확인
//                                                                    if (alarmManager.canScheduleExactAlarms()) {
//                                                                        gViewModel.dialogOpen(true)
//                                                                    } else { // 설정되어 있지 않으면 설정화면으로 이동
//
//                                                                    }
//                                                                } else { // 알림 설정화면 다이얼로그 오픈
//                                                                    gViewModel.dialogOpen(true)
//                                                                }
//                                                            } else {
//                                                                // 푸쉬 알림을 보낼 수 없는 경우
//                                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//
//                                                                    val intent =
//                                                                        Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
//                                                                            this.putExtra(
//                                                                                Settings.EXTRA_APP_PACKAGE,
//                                                                                applicationContext.packageName
//                                                                            )
//                                                                        }
//                                                                    startActivity(intent)
//                                                                }
//                                                            }