package com.nikealarm.core.domain.usecase

import com.nikealarm.core.domain.repository.ProductRepository
import javax.inject.Inject

class GetProductInfoUseCase @Inject constructor(
    private val productRepository: ProductRepository
) {
    suspend operator fun invoke(productId: String, slug: String) =
        productRepository.getProductInfo(productId, slug)
}