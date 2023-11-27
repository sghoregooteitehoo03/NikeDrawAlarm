package com.nikealarm.nikedrawalarm.presentation.productScreen

import com.nikealarm.nikedrawalarm.domain.model.Product
import com.nikealarm.nikedrawalarm.domain.model.ProductCategory

sealed interface ProductUiEvent {
    data class ChangeProductCategory(val selectedCategory: ProductCategory) : ProductUiEvent
    data class ProductItemClick(val product: Product) : ProductUiEvent
    data object ChangedProductCategory : ProductUiEvent
}
