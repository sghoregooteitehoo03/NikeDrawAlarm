package com.nikealarm.core.domain.usecase

import com.nikealarm.core.domain.repository.DatabaseRepository
import com.nikealarm.core.model.JoinedProductType
import javax.inject.Inject

class GetPagingJoinedProductUseCase @Inject constructor(
    private val databaseRepository: DatabaseRepository
) {
    operator fun invoke(joinedCategory: JoinedProductType) =
        databaseRepository.getPagingJoinedProduct(joinedCategory)
}