package com.nikealarm.nikedrawalarm.domain.usecase

import com.nikealarm.nikedrawalarm.data.repository.ProductDatabaseRepository
import com.nikealarm.nikedrawalarm.domain.model.JoinedProductType
import javax.inject.Inject

class GetPagingJoinedProductUseCase @Inject constructor(
    private val repository: ProductDatabaseRepository
) {
    operator fun invoke(joinedCategory: JoinedProductType) =
        repository.getPagingJoinedProduct(joinedCategory)
}