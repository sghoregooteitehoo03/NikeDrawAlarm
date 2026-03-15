package com.nikealarm.core.domain.usecase

import com.nikealarm.core.domain.repository.DatabaseRepository
import com.nikealarm.core.model.ProductInfo
import javax.inject.Inject

class InsertLatestUseCase @Inject constructor(
    private val databaseRepository: DatabaseRepository
) {
    suspend operator fun invoke(productInfo: ProductInfo) = databaseRepository.insertLatestData(productInfo)
}