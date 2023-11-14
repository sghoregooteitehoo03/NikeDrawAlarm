package com.nikealarm.nikedrawalarm.domain.usecase

import com.nikealarm.nikedrawalarm.data.repository.ProductRepository
import com.nikealarm.nikedrawalarm.domain.model.ProductInfo
import javax.inject.Inject

class InsertFavoriteUseCase @Inject constructor(
    private val repository: ProductRepository
) {

    suspend operator fun invoke(productInfo: ProductInfo, isFavorite: Boolean) =
        if (isFavorite) {
            repository.deleteFavoriteData(productId = productInfo.productId)
        } else {
            repository.insertFavoriteData(productInfo = productInfo)
        }

}