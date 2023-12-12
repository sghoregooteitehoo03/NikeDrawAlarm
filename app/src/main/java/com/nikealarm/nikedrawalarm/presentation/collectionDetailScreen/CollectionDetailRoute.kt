package com.nikealarm.nikedrawalarm.presentation.collectionDetailScreen

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nikealarm.nikedrawalarm.domain.model.Product
import com.nikealarm.nikedrawalarm.domain.model.ProductInfo
import com.nikealarm.nikedrawalarm.presentation.ui.DisposableEffectWithLifeCycle

@Composable
fun CollectionDetailRoute(
    viewModel: CollectionDetailViewModel = hiltViewModel(),
    onProductItemClick: (ProductInfo) -> Unit,
    onCreate: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    DisposableEffectWithLifeCycle(
        onCreate = onCreate,
        onDispose = {}
    )

    CollectionDetailScreen(
        state = state,
        onLearnMoreClick = { url ->
            openCustomTabs(context, url)
        },
        onProductItemClick = onProductItemClick
    )
}

private fun openCustomTabs(context: Context, url: String) {
    try {
        CustomTabsIntent.Builder()
            .build()
            .launchUrl(context, Uri.parse(url))
    } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(context, "크롬 브라우저가 존재하지 않습니다.", Toast.LENGTH_SHORT)
            .show()
    }
}