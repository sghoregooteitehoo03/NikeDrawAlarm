package com.nikealarm.core.domain.usecase

import com.nikealarm.core.domain.repository.DatabaseRepository
import com.nikealarm.core.model.ProductInfo
import javax.inject.Inject

class InsertFavoriteUseCase @Inject constructor(
    private val databaseRepository: DatabaseRepository
) {
    suspend operator fun invoke(productInfo: ProductInfo, isFavorite: Boolean) =
        if (isFavorite) {
            databaseRepository.deleteFavoriteData(productId = productInfo.productId)
        } else {
            databaseRepository.insertFavoriteData(productInfo = productInfo)
        }

}