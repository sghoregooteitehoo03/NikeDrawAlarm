package com.nikealarm.nikedrawalarm.presentation.upcomingScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.rememberImagePainter
import com.nikealarm.nikedrawalarm.domain.model.ProductCategory
import com.nikealarm.nikedrawalarm.domain.model.ProductInfo
import com.plcoding.cryptocurrencyappyt.presentation.ui.theme.LightGray
import com.plcoding.cryptocurrencyappyt.presentation.ui.theme.Shapes
import com.plcoding.cryptocurrencyappyt.presentation.ui.theme.Typography
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Locale

// TODO: shimmer 구현
@Composable
fun UpcomingScreen(
    state: UpcomingUiState,
    onProductClick: (ProductInfo) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        val products = state.products?.collectAsLazyPagingItems()
        products?.let {
            LazyColumn {
                items(products.itemCount) { index ->
                    UpcomingProductItem(
                        modifier = Modifier.fillMaxWidth(),
                        productInfo = products[index]!!,
                        onItemClick = onProductClick,
                        paddingValues = PaddingValues(start = 14.dp, end = 14.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun UpcomingProductItem(
    modifier: Modifier = Modifier,
    productInfo: ProductInfo,
    onItemClick: (ProductInfo) -> Unit = {},
    paddingValues: PaddingValues
) {
    Column(
        modifier.clickable { onItemClick(productInfo) }
    ) {
        Spacer(modifier = Modifier.height(14.dp))
        Text(
            text = SimpleDateFormat(
                "M. d. a hh:mm",
                Locale.KOREA
            ).format(productInfo.eventDate) +
                    if (productInfo.category == ProductCategory.Draw) "응모" else "출시",
            style = Typography.h3.copy(fontWeight = FontWeight.ExtraBold),
            modifier = Modifier.padding(paddingValues)
        )
        Spacer(modifier = Modifier.height(14.dp))
        ProductImages(images = productInfo.images, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = productInfo.title,
            style = Typography.h4.copy(fontWeight = FontWeight.Medium),
            modifier = Modifier.padding(paddingValues)
        )
        Text(
            text = productInfo.subTitle,
            style = Typography.h5.copy(fontWeight = FontWeight.Normal),
            modifier = Modifier.padding(paddingValues)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = DecimalFormat("₩#,###").format(productInfo.price),
            style = Typography.h5,
            modifier = Modifier.padding(paddingValues)
        )
        Spacer(modifier = Modifier.height(14.dp))
        Divider(thickness = 2.dp, color = LightGray)
    }
}

@Composable
fun ProductImages(
    modifier: Modifier = Modifier,
    images: List<String>
) {
    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(start = 14.dp, end = 6.dp)
    ) {
        items(images) { image ->
            Image(
                painter = rememberImagePainter(image),
                contentDescription = image,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(160.dp)
                    .clip(Shapes.large)
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
    }
}