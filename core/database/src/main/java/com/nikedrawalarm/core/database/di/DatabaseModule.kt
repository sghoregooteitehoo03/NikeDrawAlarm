package com.nikedrawalarm.core.database.di

import android.content.Context
import androidx.room.Room
import com.nikedrawalarm.core.database.ProductDatabase
import com.nikedrawalarm.core.database.dao.ProductDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object DatabaseModule {

    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): ProductDatabase =
        Room.databaseBuilder(
            context,
            ProductDatabase::class.java,
            "ProductDB"
        )
            .fallbackToDestructiveMigration()
            .build()

    @Singleton
    @Provides
    fun provideDao(database: ProductDatabase): ProductDao =
        database.getDao()
}