package com.nikealarm.nikedrawalarm.domain.model

import com.nikealarm.nikedrawalarm.data.model.LaunchView
import com.nikealarm.nikedrawalarm.data.model.MerchProduct
import com.nikealarm.nikedrawalarm.data.model.entity.ProductEntity
import java.text.SimpleDateFormat
import java.util.Locale

data class ProductInfo(
    val productId: String,
    val title: String,
    val subTitle: String,
    val price: Int,
    val images: List<String>,
    val eventDate: Long,
    val explains: String,
    val sizes: List<String>,
    val url: String,
    val category: ProductCategory
) {
    fun getProductEntity() =
        ProductEntity(
            productId = productId,
            title = title,
            subTitle = subTitle,
            price = price,
            thumbnailImage = images[0],
            eventDate = eventDate,
            url = url,
            category = category.text
        )
}

object ProductState {
    const val PRODUCT_STATUS_ACTIVE = "ACTVICE"
    const val PRODUCT_STATUS_INACTIVE = "INACTIVE"
}

sealed class ProductCategory(val text: String = "") {
    object All : ProductCategory("All")
    object Feed : ProductCategory()
    object SoldOut : ProductCategory()
    object Coming : ProductCategory("Coming")
    object Draw : ProductCategory("Draw")
}

fun getDateToLong(date: String?): Long {
    if (date == null) {
        return 0L
    }

    val dateFormat = SimpleDateFormat(
        "yyyy-MM-dd'T'hh:mm:ss.SSS'Z'",
        Locale.KOREA
    )

    return (dateFormat.parse(date)?.time?.plus(32400000) ?: 0L)  // (+9 Hours) UTC -> KOREA
}

// TODO: 상품 카테고리가 올바르게 설정되지 않는 버그 수정
fun getShoesCategory(
    merchProduct: MerchProduct,
    launchView: LaunchView?
): ProductCategory {
    return if (launchView != null) {
        if (merchProduct.commerceEndDate == null && launchView.stopEntryDate != null) {
            ProductCategory.Draw
        } else {
            if (merchProduct.status == ProductState.PRODUCT_STATUS_INACTIVE && merchProduct.commerceEndDate != null) {
                ProductCategory.SoldOut
            } else {
                ProductCategory.Coming
            }
        }
    } else {
        ProductCategory.Feed
    }
}