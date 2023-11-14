package com.nikealarm.nikedrawalarm.di

import android.content.Context
import androidx.room.Room
import com.nikealarm.nikedrawalarm.data.repository.database.ProductDatabase
import com.nikealarm.nikedrawalarm.util.AlarmBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideAlarmBuilder(@ApplicationContext context: Context) =
        AlarmBuilder(context)

    @Singleton
    @Provides
    fun provideRetrofit() =
        Retrofit.Builder()
            .client(OkHttpClient.Builder().apply {
                readTimeout(2, TimeUnit.MINUTES)
            }.build())
            .addConverterFactory(GsonConverterFactory.create())

    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context) =
        Room.databaseBuilder(
            context,
            ProductDatabase::class.java,
            "ProductDB"
        ).build()

    @Singleton
    @Provides
    fun provideDao(database: ProductDatabase) =
        database.getDao()
}