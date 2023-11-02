package com.nikealarm.nikedrawalarm.presentation.ui

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
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.nikealarm.nikedrawalarm.presentation.collectionDetailScreen.CollectionDetailRoute
import com.nikealarm.nikedrawalarm.presentation.productDetailScreen.ProductDetailRoute
import com.nikealarm.nikedrawalarm.presentation.productScreen.ProductRoute
import com.plcoding.cryptocurrencyappyt.presentation.ui.theme.Black
import com.plcoding.cryptocurrencyappyt.presentation.ui.theme.NikeDrawAssistant
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val gViewModel by viewModels<GlobalViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
                                    .padding(start = 14.dp, end = 14.dp),
                                title = if (currentRoute == UiScreen.CollectionDetailScreen.route
                                    || currentRoute == UiScreen.ProductDetailScreen.route
                                ) {
                                    ""
                                } else {
                                    "Product"
                                },
                                navigationIcon = {
                                    if (currentRoute != UiScreen.ProductScreen.route) {
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
                                },
                                actionIcon = {
                                    when (currentRoute) {
                                        UiScreen.ProductScreen.route -> {
                                            Icon(
                                                imageVector = Icons.Default.Settings,
                                                contentDescription = "설정",
                                                modifier = it.size(24.dp),
                                                tint = Black
                                            )
                                        }

                                        UiScreen.ProductDetailScreen.route -> {
                                            if (gViewModel.getProductInfoData()?.eventDate != 0L) {
                                                val state by gViewModel.notificationEntity.collectAsStateWithLifecycle()
                                                val icon = if (state != null) {
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
                                onCreate = { gViewModel.sendProductData(null) }
                            )
                        }
                        composable(route = UiScreen.ProductDetailScreen.route) {
                            ProductDetailRoute(
                                sendProductInfo = gViewModel.getProductInfoData(),
                                onCreate = {
                                    gViewModel.getNotificationData(
                                        gViewModel.getProductInfoData()?.productId ?: ""
                                    )
                                },
                                onDispose = { gViewModel.sendProductInfoData(null) }
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
}