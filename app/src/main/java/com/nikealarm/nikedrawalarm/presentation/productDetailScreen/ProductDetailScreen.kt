@file:OptIn(ExperimentalFoundationApi::class)

package com.nikealarm.nikedrawalarm.presentation.productDetailScreen

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import coil.compose.rememberImagePainter
import com.nikealarm.nikedrawalarm.domain.model.ProductCategory
import com.nikealarm.nikedrawalarm.presentation.ui.BorderedBox
import com.plcoding.cryptocurrencyappyt.presentation.ui.theme.Black
import com.plcoding.cryptocurrencyappyt.presentation.ui.theme.Gray
import com.plcoding.cryptocurrencyappyt.presentation.ui.theme.LightGray
import com.plcoding.cryptocurrencyappyt.presentation.ui.theme.NikeDrawAssistant
import com.plcoding.cryptocurrencyappyt.presentation.ui.theme.Typography
import com.plcoding.cryptocurrencyappyt.presentation.ui.theme.White
import kotlinx.coroutines.delay
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun ProductDetailScreen(
    state: ProductDetailUiState,
    onEvent: (ProductDetailUiEvent) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        if (state.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = Color.Black
            )
        } else {
            val productInfo = state.productInfo

            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
            ) {
                ImagePager(
                    images = productInfo?.images ?: listOf(),
                    modifier = Modifier
                        .fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))
                ProductInformation(
                    title = productInfo?.title ?: "",
                    subTitle = productInfo?.subTitle ?: "",
                    price = DecimalFormat("₩#,###").format(productInfo?.price ?: 0),
                    eventDate = productInfo?.eventDate ?: 0L,
                    category = productInfo?.category ?: ProductCategory.Feed,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 14.dp, end = 14.dp)
                )
                Spacer(modifier = Modifier.height(24.dp))
                if ((productInfo?.explains ?: "").isNotEmpty()) {
                    Explains(
                        explain = productInfo?.explains ?: "",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 14.dp, end = 14.dp)
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }
                MoreInfoList(
                    modifier = Modifier
                        .fillMaxWidth(),
                    listExplain = "사이즈 목록",
                    listScope = {
                        item {
                            Spacer(modifier = Modifier.width(14.dp))
                        }
                        items(productInfo?.sizes ?: listOf("")) { size ->
                            ProductSizeItem(size = size)
                            Spacer(modifier = Modifier.width(12.dp))
                        }
                        item {
                            Spacer(modifier = Modifier.width(2.dp))
                        }
                    }
                )
                Spacer(modifier = Modifier.height((24 + 54).dp))
            }
            ProductButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter),
                isFavorite = state.isFavorite,
                onFavoriteClick = { onEvent(ProductDetailUiEvent.ClickFavorite) },
                onLearnMoreClick = {
                    onEvent(
                        ProductDetailUiEvent.ClickLearnMore(productInfo?.url ?: "")
                    )
                }
            )
        }
    }
}

@Composable
fun ImagePager(
    modifier: Modifier = Modifier,
    images: List<String>
) {
    val pagerState = rememberPagerState(pageCount = { images.size })
    Box(modifier = modifier) {
        HorizontalPager(
            state = pagerState
        ) { index ->
            Image(
                painter = rememberImagePainter(data = images[index]),
                contentDescription = "",
                modifier = Modifier.aspectRatio(1f),
                contentScale = ContentScale.Crop
            )
        }

        if (images.size > 1) {
            ImageDivider(
                maxPage = images.size,
                currentPage = pagerState.currentPage,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
            )
        }
    }
}

@Composable
fun ImageDivider(
    modifier: Modifier = Modifier,
    maxPage: Int,
    currentPage: Int
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp

    LazyRow(modifier = modifier.fillMaxWidth()) {
        items(maxPage) { index ->
            if (currentPage == index) {
                Divider(
                    modifier = Modifier.width((screenWidth / maxPage).dp),
                    color = Color.Black,
                    thickness = 2.dp
                )
            } else {
                Divider(
                    modifier = Modifier.width((screenWidth / maxPage).dp),
                    color = Color.Transparent,
                    thickness = 2.dp
                )
            }
        }
    }
}

@Composable
fun ProductInformation(
    modifier: Modifier = Modifier,
    title: String,
    subTitle: String,
    price: String,
    eventDate: Long = 0L,
    category: ProductCategory
) {
    Column(modifier = modifier) {
        Text(
            text = title,
            style = Typography.h3
        )
        Text(
            text = subTitle,
            style = Typography.h4.copy(fontWeight = FontWeight.Normal)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = price,
                style = Typography.h4
            )
            Spacer(modifier = Modifier.width(16.dp))
            if (eventDate != 0L) {
                Icon(
                    imageVector = Icons.Default.Event,
                    contentDescription = "이벤트 날짜",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(2.dp))
                Text(
                    text = SimpleDateFormat(
                        "M. d. a hh:mm" +
                                when (category) {
                                    ProductCategory.Draw -> "응모"
                                    ProductCategory.Coming -> "출시"
                                    else -> ""
                                },
                        Locale.KOREA
                    ).format(eventDate),
                    style = Typography.h4
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        Divider(
            color = LightGray,
            thickness = 2.dp
        )
    }
}

@Composable
fun Explains(
    modifier: Modifier = Modifier,
    explain: String
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = explain,
            style = Typography.h5.copy(fontWeight = FontWeight.Normal),
            textAlign = TextAlign.Center,
            lineHeight = TextUnit(26f, type = TextUnitType.Sp)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Divider(
            color = LightGray,
            thickness = 2.dp
        )
    }
}

@Composable
fun MoreInfoList(
    modifier: Modifier = Modifier,
    listExplain: String,
    listScope: LazyListScope.() -> Unit
) {
    Column(modifier = modifier) {
        Text(
            text = listExplain,
            style = Typography.h4.copy(fontWeight = FontWeight.Medium),
            modifier = Modifier.padding(start = 14.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        LazyRow(content = listScope)
    }
}

@Composable
fun ProductSizeItem(
    modifier: Modifier = Modifier,
    size: String
) {
    BorderedBox(
        modifier = modifier.defaultMinSize(minWidth = 64.dp),
        text = size,
        textSize = 18.sp,
        paddingValue = PaddingValues(start = 16.dp, end = 16.dp, top = 6.dp, bottom = 6.dp)
    )
}

@Composable
fun ProductButton(
    modifier: Modifier = Modifier,
    isFavorite: Boolean,
    favoriteEnabled: Boolean = true,
    onFavoriteClick: () -> Unit = {},
    onLearnMoreClick: () -> Unit = {}
) {
    ConstraintLayout(modifier = modifier) {
        val (favorite, learnMore) = createRefs()

        Box(
            modifier = Modifier
                .constrainAs(favorite) {
                    start.linkTo(parent.start)
                    bottom.linkTo(parent.bottom)
                }
                .size(54.dp)
                .background(color = Gray)
                .then(
                    if (favoriteEnabled) {
                        Modifier.clickable { onFavoriteClick() }
                    } else {
                        Modifier
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            val icon = if (isFavorite) {
                Icons.Default.Favorite
            } else {
                Icons.Default.FavoriteBorder
            }
            val size = remember { Animatable(1f) }
            var isInit by remember { mutableStateOf(true) }

            // LaunchedEffect를 사용하여 바운드 효과를 주는 애니메이션 추가
            LaunchedEffect(isFavorite) {
                if (isFavorite && !isInit) {
                    size.animateTo(
                        1.25f,
                        animationSpec = spring(stiffness = Spring.StiffnessMedium)
                    )
                    delay(50)
                    size.animateTo(1f, animationSpec = spring(stiffness = Spring.StiffnessMedium))
                }
                isInit = false
            }

            Icon(
                imageVector = icon,
                contentDescription = "Favorite",
                modifier = Modifier.size(24.dp * size.value),
                tint = if (favoriteEnabled) {
                    Color.Red
                } else {
                    Color.Red.copy(alpha = 0.4f)
                }
            )
        }
        Box(
            modifier = Modifier
                .constrainAs(learnMore) {
                    start.linkTo(favorite.end)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                    width = Dimension.fillToConstraints
                }
                .height(54.dp)
                .background(color = Black)
                .clickable { onLearnMoreClick() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "자세히 보기",
                style = Typography.h3.copy(fontWeight = FontWeight.ExtraBold),
                color = White
            )
        }
    }
}

@Composable
fun BouncingIcon() {
    var isFavorite by remember { mutableStateOf(false) }

    // Animatable을 사용하여 크기를 애니메이트
    val size = remember { Animatable(1f) }

    // LaunchedEffect를 사용하여 바운드 효과를 주는 애니메이션 추가
    LaunchedEffect(isFavorite) {
        if (isFavorite) {
            size.animateTo(1.25f, animationSpec = spring(stiffness = Spring.StiffnessMedium))
            delay(50)
            size.animateTo(1f, animationSpec = spring(stiffness = Spring.StiffnessMedium))
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 아이콘에 Modifier.graphicsLayer를 사용하여 크기 조절
        if (isFavorite) {
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = null,
                modifier = Modifier
                    .size(48.dp * size.value)
                    .clickable {
                        isFavorite = !isFavorite
                    }
            )
        } else {
            Icon(
                imageVector = Icons.Default.FavoriteBorder,
                contentDescription = null,
                modifier = Modifier
                    .size(48.dp)
                    .clickable {
                        isFavorite = !isFavorite
                    }
            )
        }
    }
}

@Preview
@Composable
fun BouncingIconPreview() {
    NikeDrawAssistant {
        BouncingIcon()
    }
}