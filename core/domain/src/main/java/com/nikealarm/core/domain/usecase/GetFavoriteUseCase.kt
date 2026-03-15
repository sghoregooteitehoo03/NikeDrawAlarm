package com.nikealarm.core.domain.usecase

import com.nikealarm.core.domain.repository.DatabaseRepository
import javax.inject.Inject

class GetFavoriteUseCase @Inject constructor(
    private val databaseRepository: DatabaseRepository
) {

    operator fun invoke(productId: String) =
        databaseRepository.getFavoriteData(productId)
}