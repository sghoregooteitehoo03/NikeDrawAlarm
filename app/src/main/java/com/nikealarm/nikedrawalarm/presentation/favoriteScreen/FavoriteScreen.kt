package com.nikealarm.nikedrawalarm.presentation.favoriteScreen

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.nikealarm.nikedrawalarm.data.model.entity.LatestProductEntity
import com.nikealarm.nikedrawalarm.data.model.entity.ProductEntity
import com.nikealarm.nikedrawalarm.presentation.ui.ProductInfoItemRow
import com.plcoding.cryptocurrencyappyt.presentation.ui.theme.NikeDrawAssistant
import com.plcoding.cryptocurrencyappyt.presentation.ui.theme.Shapes
import com.plcoding.cryptocurrencyappyt.presentation.ui.theme.TextGray
import com.plcoding.cryptocurrencyappyt.presentation.ui.theme.Typography
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun FavoriteScreen(
    state: FavoriteUiState,
    onProductClick: (ProductEntity) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        when (state) {
            is FavoriteUiState.Success -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    ProductColumn(
                        title = "최근에 본 제품",
                        subTitle = "전체 보기",
                        content = {
                            LatestProductList(
                                latestProducts = state.latestProducts,
                                onProductClick = onProductClick
                            )
                        }
                    )
                    ProductColumn(
                        title = "알림 설정한 제품",
                        subTitle = "전체 보기",
                        content = {
                            state.notifyProducts.forEach { notifyProduct ->
                                ProductInfoItemRow(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            onProductClick(notifyProduct.productEntity)
                                        }
                                        .padding(14.dp),
                                    thumbnailImage = notifyProduct.productEntity.thumbnailImage,
                                    title = notifyProduct.productEntity.title,
                                    subTitle = notifyProduct.productEntity.subTitle,
                                    explain = SimpleDateFormat(
                                        if (notifyProduct.notificationEntity.notificationDate > 3600000L) {
                                            "h시간 m분 전에 알림"
                                        } else {
                                            "m분 전에 알림"
                                        },
                                        Locale.KOREA
                                    )
                                        .format(
                                            notifyProduct.notificationEntity.notificationDate.minus(
                                                32400000L
                                            )
                                        )
                                )
                            }
                        }
                    )
                    ProductColumn(
                        title = "좋아요 한 제품",
                        subTitle = "전체 보기",
                        content = {
                            state.favoriteProducts.forEach { favoriteProduct ->
                                ProductInfoItemRow(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            onProductClick(favoriteProduct.productEntity)
                                        }
                                        .padding(14.dp),
                                    thumbnailImage = favoriteProduct.productEntity.thumbnailImage,
                                    title = favoriteProduct.productEntity.title,
                                    subTitle = favoriteProduct.productEntity.subTitle,
                                    explain = SimpleDateFormat(
                                        "yy.MM.dd 추가 됨",
                                        Locale.KOREA
                                    ).format(
                                        favoriteProduct.favoriteEntity.favoriteDate.minus(
                                            32400000L
                                        )
                                    )
                                )
                            }
                        }
                    )
                }
            }

            is FavoriteUiState.Loading -> {}
        }
    }
}

@Composable
fun ProductColumn(
    modifier: Modifier = Modifier,
    title: String,
    subTitle: String,
    content: @Composable () -> Unit,
    onSubTitleClick: () -> Unit = {},
    onProductClick: (ProductEntity) -> Unit = {}
) {
    Column(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Text(
                text = title,
                style = Typography.h4,
                modifier = Modifier.align(Alignment.CenterStart)
            )
            Text(
                text = subTitle,
                style = Typography.subtitle1.copy(fontWeight = FontWeight.Medium, color = TextGray),
                modifier = Modifier.align(Alignment.CenterEnd)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        content()
        Spacer(modifier = Modifier.height(4.dp))
    }
}

@Composable
fun LatestProductList(
    modifier: Modifier = Modifier,
    latestProducts: List<LatestProductEntity>,
    onProductClick: (ProductEntity) -> Unit
) {
    LazyRow(modifier = modifier) {
        item {
            Spacer(modifier = Modifier.width(14.dp))
        }
        items(latestProducts) { latestProduct ->
            LatestProductItem(
                productEntity = latestProduct.productEntity,
                onProductClick = onProductClick
            )
            Spacer(modifier = Modifier.width(12.dp))
        }
        item {
            Spacer(modifier = Modifier.width(2.dp))
        }
    }
}

@Composable
fun LatestProductItem(
    modifier: Modifier = Modifier,
    productEntity: ProductEntity,
    onProductClick: (ProductEntity) -> Unit = {}
) {
    Column(
        modifier = modifier
            .width(120.dp)
            .clip(Shapes.medium)
            .clickable {
                onProductClick(productEntity)
            }
    ) {
        Image(
            painter = rememberImagePainter(data = productEntity.thumbnailImage),
            contentDescription = productEntity.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(120.dp)
                .clip(Shapes.large)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = productEntity.title,
            style = Typography.h5,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(start = 4.dp, end = 4.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = productEntity.subTitle,
            style = Typography.body1,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(start = 4.dp, end = 4.dp, bottom = 4.dp)
        )
    }
}

@Preview
@Composable
fun ProductColumnPreview() {
    NikeDrawAssistant {
        ProductColumn(
            title = "최근에 본 제품",
            subTitle = "전체 보기",
            content = { }
        )
    }
}

@Preview
@Composable
fun ProductInfoItemRowPreview() {
    NikeDrawAssistant {
        ProductInfoItemRow(
            modifier = Modifier
                .fillMaxWidth(),
            thumbnailImage = "",
            title = "Color of the Month",
            subTitle = "에어 포스 1 로우",
            explain = "1시간 10분 전에 알림"
        )
    }
}