package com.nikealarm.nikedrawalarm.domain.usecase

import com.nikealarm.nikedrawalarm.data.model.entity.FavoriteProductEntity
import com.nikealarm.nikedrawalarm.data.model.entity.LatestProductEntity
import com.nikealarm.nikedrawalarm.data.model.entity.NotifyProductEntity
import com.nikealarm.nikedrawalarm.data.repository.ProductRepository
import com.nikealarm.nikedrawalarm.presentation.favoriteScreen.FavoriteUiState
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class GetCombineProductsUseCase @Inject constructor(
    private val repository: ProductRepository
) {

    operator fun invoke(
        latestLimit: Int,
        notifyLimit: Int,
        favoriteLimit: Int,
        transform: suspend (List<LatestProductEntity>, List<NotifyProductEntity>, List<FavoriteProductEntity>) -> FavoriteUiState.Success
    ) =
        combine(
            repository.getLatestProductsData(latestLimit),
            repository.getNotifyProductsData(notifyLimit),
            repository.getFavoriteProductsData(favoriteLimit),
            transform
        )
}