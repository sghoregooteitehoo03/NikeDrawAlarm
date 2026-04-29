package com.nikealarm.core.network.di

import com.nikealarm.core.network.NetworkDataSource
import com.nikealarm.core.network.retrofit.RetrofitNetwork
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal abstract class NetworkModule {

    @Binds
    abstract fun bindsNetwork(impl: RetrofitNetwork): NetworkDataSource
}