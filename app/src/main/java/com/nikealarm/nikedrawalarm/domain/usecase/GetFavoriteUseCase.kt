package com.nikealarm.nikedrawalarm.domain.usecase

import com.nikealarm.nikedrawalarm.data.repository.ProductRepository
import com.nikealarm.nikedrawalarm.util.asResult
import javax.inject.Inject

class GetFavoriteUseCase @Inject constructor(
    private val repository: ProductRepository
) {

    operator fun invoke(productId: String) =
        repository.getFavoriteData(productId)
}