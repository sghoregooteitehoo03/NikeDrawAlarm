package com.nikealarm.nikedrawalarm.presentation.upcomingScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.rememberImagePainter
import com.nikealarm.nikedrawalarm.domain.model.ProductCategory
import com.nikealarm.nikedrawalarm.domain.model.ProductInfo
import com.plcoding.cryptocurrencyappyt.presentation.ui.theme.LightGray
import com.plcoding.cryptocurrencyappyt.presentation.ui.theme.LightSky
import com.plcoding.cryptocurrencyappyt.presentation.ui.theme.Shapes
import com.plcoding.cryptocurrencyappyt.presentation.ui.theme.TextGray
import com.plcoding.cryptocurrencyappyt.presentation.ui.theme.Typography
import com.valentinilk.shimmer.shimmer
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun UpcomingScreen(
    modifier: Modifier = Modifier,
    state: UpcomingUiState,
    onProductClick: (ProductInfo) -> Unit
) {
    Box(modifier = modifier.fillMaxSize()) {
        val products = state.products?.collectAsLazyPagingItems()
        products?.let {
            val isLoading by remember { derivedStateOf { products.loadState.refresh is LoadState.Loading } }
            val isError by derivedStateOf { products.loadState.refresh is LoadState.Error }

            if (!isError) {
                LazyColumn {
                    if (!isLoading) {
                        items(products.itemCount) { index ->
                            UpcomingProductItem(
                                modifier = Modifier.fillMaxWidth(),
                                productInfo = products[index]!!,
                                onItemClick = onProductClick,
                                paddingValues = PaddingValues(start = 14.dp, end = 14.dp)
                            )
                        }
                    } else {
                        items(3) {
                            UpcomingProductItemShimmer(
                                modifier = Modifier.padding(14.dp)
                            )
                        }
                    }
                }
            } else {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "오류가 발생하였습니다.",
                        style = Typography.h2.copy(color = TextGray)
                    )
                    Spacer(modifier = Modifier.height(18.dp))
                    Text(
                        text = "재시도",
                        style = Typography.h2.copy(color = LightSky, fontSize = 20.sp),
                        modifier = Modifier
                            .clickable { products.retry() }
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
fun UpcomingProductItemShimmer(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .shimmer()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(24.dp)
                .clip(Shapes.small)
                .background(
                    color = Color.Gray,
                    shape = Shapes.small
                )
        )
        Spacer(modifier = Modifier.height(20.dp))
        Row {
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .clip(Shapes.large)
                    .background(
                        color = Color.Gray,
                        shape = Shapes.large
                    )
            )
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .clip(Shapes.large)
                    .background(
                        color = Color.Gray,
                        shape = Shapes.large
                    )
            )
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .clip(Shapes.large)
                    .background(
                        color = Color.Gray,
                        shape = Shapes.large
                    )
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(20.dp)
                .clip(Shapes.small)
                .background(
                    color = Color.Gray,
                    shape = Shapes.small
                )
        )
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(20.dp)
                .clip(Shapes.small)
                .background(
                    color = Color.Gray,
                    shape = Shapes.small
                )
        )
        Spacer(modifier = Modifier.height(20.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .height(20.dp)
                .clip(Shapes.small)
                .background(
                    color = Color.Gray,
                    shape = Shapes.small
                )
        )
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