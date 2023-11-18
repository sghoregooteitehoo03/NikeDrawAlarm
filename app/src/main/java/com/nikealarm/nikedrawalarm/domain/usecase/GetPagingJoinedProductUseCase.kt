package com.nikealarm.nikedrawalarm.domain.usecase

import com.nikealarm.nikedrawalarm.data.repository.ProductRepository
import com.nikealarm.nikedrawalarm.domain.model.JoinedProductCategory
import javax.inject.Inject

class GetPagingJoinedProductUseCase @Inject constructor(
    private val repository: ProductRepository
) {
    operator fun invoke(joinedCategory: JoinedProductCategory) =
        repository.getPagingJoinedProduct(joinedCategory)
}