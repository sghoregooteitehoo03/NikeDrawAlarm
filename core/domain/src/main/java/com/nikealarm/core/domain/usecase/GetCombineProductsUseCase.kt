package com.nikealarm.core.domain.usecase

import com.nikealarm.core.domain.repository.DatabaseRepository
import com.nikealarm.core.model.Favorite
import com.nikealarm.core.model.Latest
import com.nikealarm.core.model.Notification
import com.nikealarm.core.model.ProductInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class GetCombineProductsUseCase @Inject constructor(
    private val databaseRepository: DatabaseRepository
) {

    data class DashboardData(
        val latestProducts: List<Pair<ProductInfo, Latest>>,
        val notifyProducts: List<Pair<ProductInfo, Notification>>,
        val favoriteProducts: List<Pair<ProductInfo, Favorite>>
    )

    operator fun invoke(latestLimit: Int, notifyLimit: Int, favoriteLimit: Int): Flow<DashboardData> {
        return combine(
            databaseRepository.getLatestProductsData(latestLimit),
            databaseRepository.getNotifyProductsData(notifyLimit),
            databaseRepository.getFavoriteProductsData(favoriteLimit)
        ) { latest, notify, favorite ->
            DashboardData(latest, notify, favorite)
        }
    }
}