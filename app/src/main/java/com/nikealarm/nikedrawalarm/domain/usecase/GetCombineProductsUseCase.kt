package com.nikealarm.nikedrawalarm.domain.usecase

import com.nikealarm.nikedrawalarm.data.model.entity.FavoriteProductEntity
import com.nikealarm.nikedrawalarm.data.model.entity.LatestProductEntity
import com.nikealarm.nikedrawalarm.data.model.entity.NotifyProductEntity
import com.nikealarm.nikedrawalarm.data.repository.ProductDatabaseRepository
import com.nikealarm.nikedrawalarm.presentation.favoriteScreen.FavoriteUiState
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class GetCombineProductsUseCase @Inject constructor(
    private val repository: ProductDatabaseRepository,
    private val getAllowNotifyUseCase: GetAllowNotifyUseCase
) {

    operator fun invoke(
        latestLimit: Int,
        notifyLimit: Int,
        favoriteLimit: Int,
        transform: suspend (Boolean, List<LatestProductEntity>, List<NotifyProductEntity>, List<FavoriteProductEntity>) -> FavoriteUiState.Success
    ) =
        combine(
            getAllowNotifyUseCase(),
            repository.getLatestProductsData(latestLimit),
            repository.getNotifyProductsData(notifyLimit),
            repository.getFavoriteProductsData(favoriteLimit),
            transform
        )
}