package com.nikealarm.nikedrawalarm.presentation.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.nikealarm.nikedrawalarm.presentation.productDetailScreen.ProductDetailRoute
import com.nikealarm.nikedrawalarm.presentation.productScreen.ProductRoute
import com.plcoding.cryptocurrencyappyt.presentation.ui.theme.NikeDrawAssistant
import com.plcoding.cryptocurrencyappyt.presentation.ui.theme.Typography
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
                        TopAppBar(
                            elevation = 0.dp,
                        ) {
                            Box(modifier = Modifier.padding(start = 14.dp, end = 14.dp)) {
                                Text(
                                    text = "Product",
                                    style = Typography.h1,
                                    color = MaterialTheme.colors.onPrimary,
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            }
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
                                onProductItemClick = {
                                    gViewModel.sendProductData(it)
                                    navController.navigate(route = UiScreen.ProductDetailScreen.route)
                                }
                            )
                        }
                        composable(route = UiScreen.ProductDetailScreen.route) {
                            ProductDetailRoute()
                        }
                    }
                }
            }
        }
    }
}