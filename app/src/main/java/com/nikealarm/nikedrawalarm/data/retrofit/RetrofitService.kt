package com.nikealarm.nikedrawalarm.data.retrofit

import com.nikealarm.nikedrawalarm.data.model.ProductDTO
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RetrofitService {
    @GET("?filter=marketplace%28KR%29&filter=language%28ko%29&filter=channelId%28010794e5-35fe-4e32-aaff-cd2c74f89d61%29&filter=exclusiveAccess%28true%2Cfalse%29")
    suspend fun getFeedProducts(
        @Query("anchor") anchor: Int,
        @Query("count") count: Int = 50
    ): ProductDTO

    @GET("?filter=marketplace%28KR%29&filter=language%28ko%29&filter=upcoming%28true%29&filter=channelId%28010794e5-35fe-4e32-aaff-cd2c74f89d61%29&filter=exclusiveAccess%28true%2Cfalse%29&sort=effectiveStartSellDateAsc")
    suspend fun getUpcomingProducts(
        @Query("anchor") anchor: Int,
        @Query("count") count: Int = 50
    ): ProductDTO

    @GET("?filter=marketplace%28KR%29&filter=language%28ko%29&filter=channelId%28010794e5-35fe-4e32-aaff-cd2c74f89d61%29&filter=exclusiveAccess%28true%2Cfalse%29")
    suspend fun getProductInfo(
        @Query("filter", encoded = true) slug: String
    ): ProductDTO
}