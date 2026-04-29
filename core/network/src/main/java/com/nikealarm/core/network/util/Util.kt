package com.nikealarm.core.network.util

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

internal fun <T> getApi(service: Class<T>): T {
    val baseUrl = "https://api.nike.com/product_feed/threads/v3/"

    return Retrofit.Builder()
        .client(
            OkHttpClient.Builder().apply {
                readTimeout(2, TimeUnit.MINUTES)
            }.build()
        )
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(baseUrl)
        .build()
        .create(service)
}