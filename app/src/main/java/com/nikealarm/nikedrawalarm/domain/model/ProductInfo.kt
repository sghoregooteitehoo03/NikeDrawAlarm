package com.nikealarm.nikedrawalarm.domain.model

import com.nikealarm.nikedrawalarm.data.model.LaunchView
import com.nikealarm.nikedrawalarm.data.model.MerchProduct
import com.nikealarm.nikedrawalarm.data.model.Objects
import com.nikealarm.nikedrawalarm.data.model.entity.ProductEntity
import com.nikealarm.nikedrawalarm.util.Constants
import java.lang.NullPointerException
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
    object SoldOut : ProductCategory("SoldOut")
    object Coming : ProductCategory("Coming")
    object Draw : ProductCategory("Draw")
}

fun translateToProductInfoList(filterProduct: Objects): List<ProductInfo> {
    return filterProduct
        .publishedContent
        .nodes
        .filter { it.subType == "carousel" } // 제품들만 필터링
        .map { nodes ->
            var explains = ""
            nodes.properties.jsonBody?.content?.forEach {
                val jsonBody = it.content

                for (i in jsonBody.indices) {
                    val content = jsonBody[i].text
                    if (content.contains("SKU")) {
                        explains += content
                        break
                    }

                    explains += "$content\n\n" // 제품에 관한 설명을 추가함
                }
            }


            val productId = nodes.properties.actions[0].product.productId

            try {
                val productInfo = filterProduct.productInfo.find {
                    it.merchProduct.id == productId
                }!!

                ProductInfo(
                    productId = productId,
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
            } catch (e: NullPointerException) {
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
}

fun getDateToLong(date: String?): Long {
    if (date == null) {
        return 0L
    }

    val dateFormat = SimpleDateFormat(
        "yyyy-MM-dd'T'hh:mm:ss.SSS'Z'",
        Locale.KOREA
    )
    val dateTime = (dateFormat.parse(date)?.time?.plus(32400000) ?: 0L)  // (+9 Hours) UTC -> KOREA

    return if (dateTime < System.currentTimeMillis()) { // 출시 기간이 지난 상품
        0L
    } else {
        dateTime
    }
}

fun getShoesCategory(
    merchProduct: MerchProduct,
    launchView: LaunchView?
): ProductCategory {
    return if (launchView != null) {
        if (launchView.stopEntryDate != null && launchView.method == "DAN") {
            if (getDateToLong(launchView.stopEntryDate) >= System.currentTimeMillis()) {
                ProductCategory.Draw
            } else {
                ProductCategory.Feed
            }
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

// 제품들에 관해서만 필터링, Test 제품 걸러내기
fun getProductFilter(objects: Objects) =
    (objects.publishedContent.properties.threadType == "product" || objects.publishedContent.properties.threadType == "multi_product")
            && objects.publishedContent.nodes.size > 1