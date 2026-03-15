package com.nikealarm.core.domain.usecase

import com.nikealarm.core.domain.repository.ProductRepository
import javax.inject.Inject

class GetPagingUpcomingUseCase @Inject constructor(
    private val productRepository: ProductRepository
) {
    operator fun invoke() =
        productRepository.getPagingUpcoming()
}