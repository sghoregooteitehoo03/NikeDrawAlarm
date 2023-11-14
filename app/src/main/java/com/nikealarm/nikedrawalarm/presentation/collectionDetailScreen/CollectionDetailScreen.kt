package com.nikealarm.nikedrawalarm.presentation.collectionDetailScreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nikealarm.nikedrawalarm.domain.model.ProductInfo
import com.nikealarm.nikedrawalarm.presentation.productDetailScreen.Explains
import com.nikealarm.nikedrawalarm.presentation.productDetailScreen.ImagePager
import com.nikealarm.nikedrawalarm.presentation.productDetailScreen.MoreInfoList
import com.nikealarm.nikedrawalarm.presentation.productDetailScreen.ProductButton
import com.nikealarm.nikedrawalarm.presentation.productDetailScreen.ProductInformation
import com.nikealarm.nikedrawalarm.presentation.ui.ProductInfoItem
import java.text.DecimalFormat

@Composable
fun CollectionDetailScreen(
    state: CollectionDetailUiState,
    onLearnMoreClick: (String) -> Unit,
    onProductItemClick: (ProductInfo) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        val collection = state.product?.collection

        Column(
            modifier = Modifier.verticalScroll(rememberScrollState())
        ) {
            ImagePager(
                images = listOf(collection?.thumbnailImage ?: ""),
                modifier = Modifier
                    .fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
            ProductInformation(
                title = collection?.title ?: "",
                subTitle = collection?.subTitle ?: "",
                price = collection?.price ?: "",
                eventDate = 0L,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 14.dp, end = 14.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            if ((collection?.explains ?: "").isNotEmpty()) {
                Explains(
                    explain = collection?.explains ?: "",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 14.dp, end = 14.dp)
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
            MoreInfoList(
                modifier = Modifier
                    .fillMaxWidth(),
                listExplain = "제품 (${state.product?.productInfoList?.size ?: 0})",
                listScope = {
                    item {
                        Spacer(modifier = Modifier.width(14.dp))
                    }
                    items(state.product?.productInfoList ?: listOf()) { productInfo ->
                        ProductInfoItem(
                            modifier = Modifier.padding(4.dp),
                            title = productInfo.title,
                            subTitle = productInfo.subTitle,
                            price = DecimalFormat("₩#,###").format(productInfo.price),
                            thumbnailImage = productInfo.images[0],
                            onClick = { onProductItemClick(productInfo) }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.width(10.dp))
                    }
                }
            )
            Spacer(modifier = Modifier.height((24 + 54).dp))
        }
        ProductButton(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            isFavorite = false,
            onLearnMoreClick = { onLearnMoreClick(collection?.url ?: "") }
        )
    }
}