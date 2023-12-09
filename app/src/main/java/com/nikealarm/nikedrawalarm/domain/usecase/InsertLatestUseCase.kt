package com.nikealarm.nikedrawalarm.domain.usecase

import com.nikealarm.nikedrawalarm.data.repository.ProductDatabaseRepository
import com.nikealarm.nikedrawalarm.domain.model.ProductInfo
import javax.inject.Inject

class InsertLatestUseCase @Inject constructor(
    private val repository: ProductDatabaseRepository
) {
    suspend operator fun invoke(productInfo: ProductInfo) {
        repository.insertLatestData(productInfo)
    }
}