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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.nikealarm.nikedrawalarm.presentation.productScreen.ProductCategories
import com.nikealarm.nikedrawalarm.presentation.ui.navigation.AppNavController
import com.nikealarm.nikedrawalarm.util.Constants
import com.plcoding.cryptocurrencyappyt.presentation.ui.theme.Black
import com.plcoding.cryptocurrencyappyt.presentation.ui.theme.Gray
import com.plcoding.cryptocurrencyappyt.presentation.ui.theme.NikeDrawAssistant
import com.plcoding.cryptocurrencyappyt.presentation.ui.theme.Typography
import com.plcoding.cryptocurrencyappyt.presentation.ui.theme.White
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

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
                val scaffoldState = rememberScaffoldState()
                val appNavController = remember { AppNavController(gViewModel, navController) }

                LaunchedEffect(key1 = gViewModel.event) {
                    gViewModel.event.collectLatest { event ->
                        when (event) {
                            is ActionEvent.ActionShowMessage -> {
                                scaffoldState.snackbarHostState
                                    .showSnackbar(event.message)
                            }

                            else -> {}
                        }
                    }
                }

                Scaffold(
                    scaffoldState = scaffoldState,
                    topBar = {
                        val backStack = navController.currentBackStackEntryAsState()
                        val currentRoute = backStack.value?.destination?.route ?: ""

                        // TODO: .refactor 분리하기
                        TopAppBar(
                            modifier = if (currentRoute == UiScreen.ProductScreen.route) {
                                Modifier.height(96.dp)
                            } else {
                                Modifier.height(54.dp)
                            }
                        ) {
                            NikeTopAppBar(
                                modifier = Modifier
                                    .padding(start = 14.dp, end = 14.dp),
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
                                                    tint = MaterialTheme.colors.onPrimary
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
                                                tint = MaterialTheme.colors.onPrimary,
                                                modifier = it
                                                    .size(24.dp)
                                                    .clickable {
                                                        appNavController.navigateToSettingScreen()
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
                                                    tint = MaterialTheme.colors.onPrimary
                                                )
                                            }
                                        }

                                        else -> {}
                                    }
                                },
                                content = {
                                    if (currentRoute == UiScreen.ProductScreen.route) {
                                        val selectedCategory by gViewModel.selectedCategory
                                        ProductCategories(
                                            modifier = Modifier
                                                .padding(
                                                    top = 14.dp
                                                ),
                                            selectedCategory = selectedCategory,
                                            onCategoryItemClick = { category ->
                                                gViewModel.setSelectedCategory(category)
                                                gViewModel.setActionEvent(
                                                    ActionEvent.ActionSelectCategory(
                                                        category
                                                    )
                                                )
                                            }
                                        )
                                    }
                                }
                            )
                        }
                    },
                    bottomBar = {
                        // TODO: .refactor 분리하기
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
                    appNavController.AppNavHost(modifier = Modifier.padding(it))
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