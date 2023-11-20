package com.nikealarm.nikedrawalarm.domain.model

import com.nikealarm.nikedrawalarm.data.model.LaunchView
import com.nikealarm.nikedrawalarm.data.model.MerchProduct
import com.nikealarm.nikedrawalarm.data.model.Objects
import com.nikealarm.nikedrawalarm.data.model.entity.ProductEntity
import com.nikealarm.nikedrawalarm.util.Constants
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
    object Feed : ProductCategory("Feed")
    object SoldOut : ProductCategory()
    object Coming : ProductCategory("Coming")
    object Draw : ProductCategory("Draw")
}

// TODO: 가격이 맞지 않는 버그
fun translateToProductInfoList(filterProduct: Objects): List<ProductInfo> {
    return filterProduct.productInfo.mapIndexed { index, productInfo ->
        val nodes = filterProduct.publishedContent.nodes.filter { it.subType == "carousel" }[index]

        val explains: String =
            nodes.properties.jsonBody?.content?.get(0)?.content?.filter {
                !it.text.contains("SNKRS")
            }?.get(0)?.text ?: ""

        try {
            ProductInfo(
                productId = productInfo.merchProduct.id,
                title = nodes.properties.title,
                subTitle = nodes.properties.subtitle,
                price = productInfo.merchPrice.currentPrice,
                images = nodes.nodes!!.map { it.properties.squarishURL },
                eventDate = getDateToLong(productInfo.launchView?.startEntryDate),
                explains = explains,
                sizes = productInfo.skus?.map {
                    it.countrySpecifications[0].localizedSize
                } ?: listOf(),
                url = Constants.NIKE_PRODUCT_URL + filterProduct.publishedContent.properties.seo.slug,
                category = getShoesCategory(
                    productInfo.merchProduct,
                    productInfo.launchView
                )
            )
        } catch (e: IndexOutOfBoundsException) {
            ProductInfo(
                productId = "",
                title = nodes.properties.title,
                subTitle = nodes.properties.subtitle,
                price = -1,
                images = nodes.nodes!!.map { it.properties.squarishURL },
                eventDate = 0L,
                explains = explains,
                sizes = listOf(),
                url = Constants.NIKE_PRODUCT_URL + filterProduct.publishedContent.properties.seo.slug,
                category = ProductCategory.Feed
            )
        }
    }
//    return filterProduct
//        .publishedContent
//        .nodes
//        .filter { it.subType == "carousel" } // 제품들만 필터링
//        .mapIndexed { index, nodes ->
//            val explains: String =
//                nodes.properties.jsonBody?.content?.get(0)?.content?.filter {
//                    !it.text.contains("SNKRS")
//                }?.get(0)?.text ?: ""
//
//            try {
//                ProductInfo(
//                    productId = filterProduct.productInfo[index].merchProduct.id,
//                    title = nodes.properties.title,
//                    subTitle = nodes.properties.subtitle,
//                    price = filterProduct.productInfo[index].merchPrice.currentPrice,
//                    images = nodes.nodes!!.map { it.properties.squarishURL },
//                    eventDate = getDateToLong(filterProduct.productInfo[index].launchView?.startEntryDate),
//                    explains = explains,
//                    sizes = filterProduct.productInfo[index].skus?.map {
//                        it.countrySpecifications[0].localizedSize
//                    } ?: listOf(),
//                    url = Constants.NIKE_PRODUCT_URL + filterProduct.publishedContent.properties.seo.slug,
//                    category = getShoesCategory(
//                        filterProduct.productInfo[index].merchProduct,
//                        filterProduct.productInfo[index].launchView
//                    )
//                )
//            } catch (e: IndexOutOfBoundsException) {
//                ProductInfo(
//                    productId = "",
//                    title = nodes.properties.title,
//                    subTitle = nodes.properties.subtitle,
//                    price = -1,
//                    images = nodes.nodes!!.map { it.properties.squarishURL },
//                    eventDate = 0L,
//                    explains = explains,
//                    sizes = listOf(),
//                    url = Constants.NIKE_PRODUCT_URL + filterProduct.publishedContent.properties.seo.slug,
//                    category = ProductCategory.Feed
//                )
//            }
//        }
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