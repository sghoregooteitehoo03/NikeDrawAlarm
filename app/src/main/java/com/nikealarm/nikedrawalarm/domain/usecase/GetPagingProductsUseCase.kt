package com.nikealarm.nikedrawalarm.domain.usecase

import androidx.paging.filter
import androidx.paging.map
import com.nikealarm.nikedrawalarm.data.repository.ProductRepository
import com.nikealarm.nikedrawalarm.domain.model.ProductCategory
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetPagingProductsUseCase @Inject constructor(
    private val repository: ProductRepository
) {
    operator fun invoke(selectedCategory: ProductCategory) =
        when (selectedCategory) {
            ProductCategory.All -> {
                repository.getPagingProducts(isUpcoming = false)
            }

            else -> {
                repository.getPagingProducts(isUpcoming = true)
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