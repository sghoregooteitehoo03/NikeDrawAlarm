package com.nikealarm.nikedrawalarm.presentation.ui

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.TopAppBar
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.nikealarm.nikedrawalarm.presentation.productScreen.ProductCategories
import com.nikealarm.nikedrawalarm.presentation.ui.navigation.AppNavController
import com.nikealarm.nikedrawalarm.util.Constants
import com.plcoding.cryptocurrencyappyt.presentation.ui.theme.NikeDrawAssistant
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val gViewModel by viewModels<GlobalViewModel>()

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

                            is ActionEvent.ActionNavigationUp -> {
                                navController.navigateUp()
                            }

                            is ActionEvent.ActionSettingIcon -> {
                                appNavController.navigateToSettingScreen()
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
                                title = getTopAppBarTitle(
                                    currentRoute = currentRoute,
                                    backStack = backStack.value
                                ),
                                navigationIcon = {
                                    NavigationIcon(
                                        modifier = it,
                                        currentRoute = currentRoute,
                                        onClick = gViewModel::setActionEvent
                                    )
                                },
                                actionIcon = {
                                    val notificationEntity by gViewModel.notificationEntity
                                    ActionIcon(
                                        modifier = it,
                                        currentRoute = currentRoute,
                                        notificationEntity = notificationEntity,
                                        onClick = gViewModel::setActionEvent
                                    )
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
                                            }
                                        )
                                    }
                                }
                            )
                        }
                    },
                    bottomBar = {
                        val backStack by navController.currentBackStackEntryAsState()
                        val currentDestination = backStack?.destination

                        NikeBottomBar(
                            currentDestination = currentDestination
                        ) { route ->
                            navController.navigate(route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
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