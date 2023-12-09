package com.nikealarm.nikedrawalarm.domain.usecase

import com.nikealarm.nikedrawalarm.data.repository.ProductRepository
import javax.inject.Inject

class GetProductInfoUseCase @Inject constructor(
    private val repository: ProductRepository
) {
    // TODO: 나중에 flow로 바꾸기
    suspend operator fun invoke(productId: String, slug: String) =
        repository.getProductInfo(productId, slug)
}