package com.nikealarm.nikedrawalarm.presentation.productScreen

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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.nikealarm.nikedrawalarm.domain.model.Product
import com.nikealarm.nikedrawalarm.domain.model.ProductCategory
import com.nikealarm.nikedrawalarm.domain.model.ProductInfo
import com.nikealarm.nikedrawalarm.presentation.ui.BorderedBox
import com.nikealarm.nikedrawalarm.presentation.ui.ProductInfoItem
import com.plcoding.cryptocurrencyappyt.presentation.ui.theme.Black
import com.plcoding.cryptocurrencyappyt.presentation.ui.theme.Gray
import com.plcoding.cryptocurrencyappyt.presentation.ui.theme.LightSky
import com.plcoding.cryptocurrencyappyt.presentation.ui.theme.NikeDrawAssistant
import com.plcoding.cryptocurrencyappyt.presentation.ui.theme.Shapes
import com.plcoding.cryptocurrencyappyt.presentation.ui.theme.TextGray
import com.plcoding.cryptocurrencyappyt.presentation.ui.theme.Typography
import com.plcoding.cryptocurrencyappyt.presentation.ui.theme.White
import com.valentinilk.shimmer.shimmer
import me.onebone.toolbar.CollapsingToolbarScaffold
import me.onebone.toolbar.ScrollStrategy
import me.onebone.toolbar.rememberCollapsingToolbarScaffoldState
import java.text.DecimalFormat

@Composable
fun ProductScreen(
    state: ProductUiState,
    listState: LazyGridState = rememberLazyGridState(),
    onEvent: (ProductUiEvent) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        val products = when (state.selectedCategory) {
            ProductCategory.All -> {
                state.allProducts?.collectAsLazyPagingItems()
            }

            ProductCategory.Coming -> {
                state.comingProducts?.collectAsLazyPagingItems()
            }

            ProductCategory.Draw -> {
                state.drawProducts?.collectAsLazyPagingItems()
            }

            else -> {
                state.allProducts?.collectAsLazyPagingItems()
            }
        }

        products?.let {
            val isLoading by derivedStateOf { products.loadState.refresh is LoadState.Loading }
            val isError by derivedStateOf { products.loadState.refresh is LoadState.Error }

            if (!isError) {
                if (!isLoading && products.itemCount == 0) {
                    Text(
                        text = "제품이 존재하지 않습니다.",
                        style = Typography.h2.copy(color = TextGray),
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else {
                    LazyVerticalGrid(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(
                                start = 8.dp,
                                end = 8.dp,
                                top = 6.dp,
                                bottom = 6.dp
                            ),
                        columns = GridCells.Fixed(2),
                        state = listState
                    ) {
                        if (!isLoading) {
                            items(products.itemCount) { index ->
                                ProductItem(
                                    product = it[index]!!,
                                    modifier = Modifier.padding(4.dp),
                                    onClick = { onEvent(ProductUiEvent.ProductItemClick(it)) }
                                )
                            }
                        } else {
                            items(8) {
                                ProductItemShimmer(modifier = Modifier.padding(4.dp))
                            }
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
fun ProductItem(
    modifier: Modifier = Modifier,
    product: Product,
    onClick: (Product) -> Unit = {}
) {
    val title = product.collection?.title ?: product.productInfoList[0].title
    val subTitle = product.collection?.subTitle ?: product.productInfoList[0].subTitle
    val price = product.collection?.price
        ?: DecimalFormat("₩#,###").format(product.productInfoList[0].price)
    val thumbnailImage = product.collection?.thumbnailImage ?: product.productInfoList[0].images[0]

    ProductInfoItem(
        modifier = modifier,
        title = title,
        subTitle = subTitle,
        price = price,
        thumbnailImage = thumbnailImage,
        blurItem = {
            if (product.collection != null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.4f), shape = Shapes.large)
                        .clip(shape = Shapes.large)
                )
                Text(
                    modifier = Modifier.align(Alignment.Center),
                    text = "+${product.productInfoList.size}",
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        },
        onClick = { onClick(product) }
    )
}

@Composable
fun ProductItemShimmer(
    modifier: Modifier = Modifier
) {
    val screenWidth = (LocalConfiguration.current.screenWidthDp / 2) - 16
    Column(
        modifier = modifier
            .width(screenWidth.dp)
            .clip(Shapes.large)
            .shimmer()
    ) {
        Box(
            modifier = Modifier
                .size(screenWidth.dp)
                .clip(Shapes.large)
                .background(
                    color = Color.Gray,
                    shape = Shapes.large
                )
        )
        Column(
            modifier = Modifier
                .width(screenWidth.dp)
                .padding(top = 8.dp, bottom = 8.dp, start = 6.dp, end = 6.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(16.dp)
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
                    .height(16.dp)
                    .clip(Shapes.small)
                    .background(
                        color = Color.Gray,
                        shape = Shapes.small
                    )
            )
            Spacer(modifier = Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .height(16.dp)
                    .clip(Shapes.small)
                    .background(
                        color = Color.Gray,
                        shape = Shapes.small
                    )
            )
        }
    }
}

@Composable
fun ProductCategories(
    modifier: Modifier = Modifier,
    selectedCategory: ProductCategory,
    onCategoryItemClick: (ProductCategory) -> Unit = {}
) {
    Row(modifier = modifier.fillMaxWidth()) {
        ProductCategoryItem(
            productCategory = ProductCategory.All,
            isSelected = selectedCategory == ProductCategory.All,
            onClick = onCategoryItemClick
        )
        Spacer(modifier = Modifier.width(12.dp))
        ProductCategoryItem(
            productCategory = ProductCategory.Coming,
            isSelected = selectedCategory == ProductCategory.Coming,
            onClick = onCategoryItemClick
        )
        Spacer(modifier = Modifier.width(12.dp))
        ProductCategoryItem(
            productCategory = ProductCategory.Draw,
            isSelected = selectedCategory == ProductCategory.Draw,
            onClick = onCategoryItemClick
        )
    }
}

@Composable
fun ProductCategoryItem(
    modifier: Modifier = Modifier,
    productCategory: ProductCategory,
    isSelected: Boolean,
    onClick: (ProductCategory) -> Unit = {}
) {
    BorderedBox(
        modifier = modifier,
        text = productCategory.text,
        textSize = 16.sp,
        textColor = if (isSelected) {
            White
        } else {
            Black
        },
        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
        borderColor = if (isSelected) {
            Black
        } else {
            Gray
        },
        backgroundColor = if (isSelected) {
            Black
        } else {
            White
        },
        paddingValue = PaddingValues(
            start = 12.dp, end = 12.dp, top = 4.dp, bottom = 4.dp
        ), onClick = {
            if (!isSelected) {
                onClick(productCategory)
            }
        }
    )
}

@Preview
@Composable
fun ProductCategoriesPreview() {
    NikeDrawAssistant {
        ProductCategories(
            selectedCategory = ProductCategory.All
        )
    }
}

@Preview
@Composable
fun ProductItemPreview() {
    NikeDrawAssistant {
        val testProduct = Product(
            collection = null,
            productInfoList = listOf(
                ProductInfo(
                    productId = "0",
                    title = "Royal Reimagined",
                    subTitle = "에어 조던 1 하이 OG",
                    price = 219000,
                    images = listOf("https://static.nike.com/a/images/t_prod_ss/w_960,c_limit,f_auto/6c2b1dc1-7614-4d1c-89d6-ab0bf6422a7f/%EC%97%90%EC%96%B4-%EC%A1%B0%EB%8D%98-1-%ED%95%98%EC%9D%B4-og-royal-reimagined-dz5485-042-%EC%B6%9C%EC%8B%9C%EC%9D%BC.jpg"),
                    eventDate = 0L,
                    explains = "",
                    sizes = listOf(),
                    url = "",
                    category = ProductCategory.Feed
                )
            )
        )

        ProductItem(product = testProduct)
    }
}