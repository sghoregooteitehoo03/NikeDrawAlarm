package com.nikealarm.nikedrawalarm.presentation.productScreen

import androidx.paging.PagingData
import com.nikealarm.nikedrawalarm.domain.model.Product
import com.nikealarm.nikedrawalarm.domain.model.ProductCategory
import kotlinx.coroutines.flow.Flow

data class ProductUiState(
    val products: Flow<PagingData<Product>>? = null,
    val selectedCategory: ProductCategory = ProductCategory.All
)

