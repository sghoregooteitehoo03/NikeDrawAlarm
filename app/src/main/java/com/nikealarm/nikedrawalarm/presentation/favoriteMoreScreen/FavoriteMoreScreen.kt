package com.nikealarm.nikedrawalarm.presentation.favoriteMoreScreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.compose.collectAsLazyPagingItems
import com.nikealarm.nikedrawalarm.data.model.entity.ProductEntity
import com.nikealarm.nikedrawalarm.presentation.ui.ProductInfoItem

// TODO: shimmer, 제품 표시 안될 때 표시할 디자인
@Composable
fun FavoriteMoreScreen(
    state: FavoriteMoreUiState,
    onProductClick: (ProductEntity) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        val products = state.products?.collectAsLazyPagingItems()
        LazyVerticalGrid(
            modifier = Modifier.padding(
                start = 8.dp,
                end = 8.dp,
                top = 6.dp,
                bottom = 6.dp
            ),
            columns = GridCells.Fixed(2)
        ) {
            products?.let {
                items(it.itemCount) { index ->
                    val joinedProduct = it[index]!!

                    ProductInfoItem(
                        modifier = Modifier.padding(4.dp),
                        title = joinedProduct.productEntity.title,
                        subTitle = joinedProduct.productEntity.subTitle,
                        price = joinedProduct.explains,
                        thumbnailImage = joinedProduct.productEntity.thumbnailImage,
                        onClick = { onProductClick(joinedProduct.productEntity) }
                    )
                }
            }
        }
    }
}