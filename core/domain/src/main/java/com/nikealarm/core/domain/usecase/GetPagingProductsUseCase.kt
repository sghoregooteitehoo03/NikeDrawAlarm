package com.nikealarm.core.domain.usecase

import androidx.paging.filter
import com.nikealarm.core.domain.repository.ProductRepository
import com.nikealarm.core.model.ProductCategory
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetPagingProductsUseCase @Inject constructor(
    private val productRepository: ProductRepository
) {
    operator fun invoke(selectedCategory: ProductCategory) =
        when (selectedCategory) {
            ProductCategory.All -> {
                productRepository.getPagingProducts(isUpcoming = false)
            }

            else -> {
                productRepository.getPagingProducts(isUpcoming = true)
                    .map { pagingData ->
                        pagingData.filter { product ->
                            product.productInfoList.any {
                                it.category == selectedCategory
                            }
                        }
                    }
            }
        }
}