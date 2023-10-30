package com.nikealarm.nikedrawalarm.domain.usecase

import com.nikealarm.nikedrawalarm.data.repository.ProductRepository
import com.nikealarm.nikedrawalarm.domain.model.ProductCategory
import javax.inject.Inject

class GetPagingProductsUseCase @Inject constructor(
    private val repository: ProductRepository
) {
    operator fun invoke(selectedCategory: ProductCategory) =
        repository.getPagingProduct(selectedCategory)
}