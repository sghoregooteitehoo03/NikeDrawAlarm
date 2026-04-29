package com.nikealarm.core.network

import com.nikealarm.core.network.model.product.ProductDTO

interface NetworkDataSource {
    suspend fun getFeedProducts(
        anchor: Int,
        count: Int = 50
    ): ProductDTO

    suspend fun getUpcomingProducts(
        anchor: Int,
        count: Int = 50
    ): ProductDTO

    suspend fun getProductInfo(
        slug: String
    ): ProductDTO
}