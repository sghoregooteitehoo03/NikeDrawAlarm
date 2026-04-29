package com.nikealarm.core.network.retrofit

import com.nikealarm.core.network.NetworkDataSource
import com.nikealarm.core.network.model.product.ProductDTO
import com.nikealarm.core.network.util.getApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class RetrofitNetwork @Inject constructor() : NetworkDataSource {
    private val api = getApi(RequestApi::class.java)

    override suspend fun getFeedProducts(
        anchor: Int,
        count: Int
    ): ProductDTO {
        return api.getFeedProducts(anchor, count)
    }

    override suspend fun getUpcomingProducts(
        anchor: Int,
        count: Int
    ): ProductDTO {
        return api.getUpcomingProducts(anchor, count)
    }

    override suspend fun getProductInfo(slug: String): ProductDTO {
        return api.getProductInfo(slug)
    }
}