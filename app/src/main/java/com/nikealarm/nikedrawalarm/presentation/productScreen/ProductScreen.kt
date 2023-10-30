package com.nikealarm.nikedrawalarm.presentation.productScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.map
import coil.compose.rememberImagePainter
import com.nikealarm.nikedrawalarm.domain.model.Product
import com.nikealarm.nikedrawalarm.domain.model.ProductCategory
import com.nikealarm.nikedrawalarm.domain.model.ProductInfo
import com.plcoding.cryptocurrencyappyt.presentation.ui.theme.Black
import com.plcoding.cryptocurrencyappyt.presentation.ui.theme.Gray
import com.plcoding.cryptocurrencyappyt.presentation.ui.theme.LightGray
import com.plcoding.cryptocurrencyappyt.presentation.ui.theme.NikeDrawAssistant
import com.plcoding.cryptocurrencyappyt.presentation.ui.theme.Shapes
import com.plcoding.cryptocurrencyappyt.presentation.ui.theme.Typography
import com.plcoding.cryptocurrencyappyt.presentation.ui.theme.White
import me.onebone.toolbar.CollapsingToolbarScaffold
import me.onebone.toolbar.ScrollStrategy
import me.onebone.toolbar.rememberCollapsingToolbarScaffoldState
import java.text.DecimalFormat

// TODO:
//  . 상품 클릭
//  . 올라가기 버튼
//  . Shimmer

@Composable
fun ProductScreen(
    state: ProductScreenState,
    onProductItemClick: (Product) -> Unit,
    onCategoryItemClick: (ProductCategory) -> Unit
) {
    val collapsingState = rememberCollapsingToolbarScaffoldState()

    CollapsingToolbarScaffold(
        modifier = Modifier.fillMaxSize(),
        state = collapsingState,
        scrollStrategy = ScrollStrategy.EnterAlways,
        toolbar = {
            Column {
                ProductCategories(
                    modifier = Modifier.padding(
                        start = 14.dp,
                        end = 14.dp,
                        top = 12.dp,
                        bottom = 12.dp
                    ),
                    selectedCategory = state.selectedCategory,
                    onCategoryItemClick = onCategoryItemClick
                )
                Divider(
                    color = LightGray,
                    thickness = 2.dp
                )
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            val products = state.products?.collectAsLazyPagingItems()

            products?.let {
                LazyVerticalGrid(
                    modifier = Modifier.padding(
                        start = 8.dp,
                        end = 8.dp,
                        top = 6.dp,
                        bottom = 6.dp
                    ),
                    columns = GridCells.Fixed(2)
                ) {
                    items(it.itemCount) { index ->
                        ProductItem(
                            product = it[index]!!,
                            modifier = Modifier.padding(6.dp),
                            onClick = onProductItemClick
                        )
                    }
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

    Column(
        modifier = modifier
            .clip(Shapes.large)
            .clickable {
                onClick(product)
            }
    ) {
        Box(modifier = Modifier.size(186.dp)) {
            Image(
                painter = rememberImagePainter(data = thumbnailImage),
                contentDescription = title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .aspectRatio(1f)
                    .fillMaxSize()
                    .clip(Shapes.large)
            )
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
        }
        Column(
            modifier = Modifier.padding(4.dp)
        ) {
            Text(
                text = title,
                style = Typography.h5,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = subTitle,
                style = Typography.body1,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = price,
                style = Typography.body1,
                fontWeight = FontWeight.Bold
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
    val textSize = 16.sp
    val paddingValue = PaddingValues(start = 12.dp, end = 12.dp, top = 4.dp, bottom = 4.dp)

    Row(modifier = modifier.fillMaxWidth()) {
        ProductCategoryItem(
            productCategory = ProductCategory.All,
            isSelected = selectedCategory == ProductCategory.All,
            textSize = textSize,
            paddingValue = paddingValue,
            onClick = onCategoryItemClick
        )
        Spacer(modifier = Modifier.width(12.dp))
        ProductCategoryItem(
            productCategory = ProductCategory.Coming,
            isSelected = selectedCategory == ProductCategory.Coming,
            textSize = textSize,
            paddingValue = paddingValue,
            onClick = onCategoryItemClick
        )
        Spacer(modifier = Modifier.width(12.dp))
        ProductCategoryItem(
            productCategory = ProductCategory.Draw,
            isSelected = selectedCategory == ProductCategory.Draw,
            textSize = textSize,
            paddingValue = paddingValue,
            onClick = onCategoryItemClick
        )
    }
}

@Composable
fun ProductCategoryItem(
    modifier: Modifier = Modifier,
    productCategory: ProductCategory,
    isSelected: Boolean,
    textSize: TextUnit,
    paddingValue: PaddingValues,
    onClick: (ProductCategory) -> Unit = {}
) {
    Box(
        modifier = modifier
            .background(
                color = if (isSelected) {
                    Black
                } else {
                    White
                },
                shape = Shapes.small
            )
            .border(
                width = 2.dp, color = if (isSelected) {
                    Black
                } else {
                    Gray
                }, shape = Shapes.small
            )
            .clip(Shapes.small)
            .clickable {
                if (!isSelected) {
                    onClick(productCategory)
                }
            }
            .padding(paddingValue)
    ) {
        Text(
            text = productCategory.text,
            color = if (isSelected) {
                White
            } else {
                Black
            },
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            fontSize = textSize
        )
    }
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