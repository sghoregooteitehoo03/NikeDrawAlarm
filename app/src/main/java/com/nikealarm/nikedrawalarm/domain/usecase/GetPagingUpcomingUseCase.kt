package com.nikealarm.nikedrawalarm.domain.usecase

import com.nikealarm.nikedrawalarm.data.repository.ProductRepository
import javax.inject.Inject

class GetPagingUpcomingUseCase @Inject constructor(
    private val repository: ProductRepository
) {
    operator fun invoke() =
        repository.getPagingUpcoming()
}