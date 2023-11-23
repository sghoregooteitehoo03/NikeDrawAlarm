package com.nikealarm.nikedrawalarm.domain.usecase

import com.nikealarm.nikedrawalarm.data.repository.ProductDatabaseRepository
import javax.inject.Inject

class ClearFavoriteProductUseCase @Inject constructor(
    private val repository: ProductDatabaseRepository
) {
    suspend operator fun invoke() {
        repository.clearFavoriteData()
    }
}