package com.nikealarm.core.datastore.di

import android.content.Context
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.preferencesDataStoreFile
import com.nikealarm.core.datastore.util.DataStoreUtil
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object DataStoreModule {
    @Provides
    @Singleton
    internal fun provideDataStore(@ApplicationContext context: Context) =
        PreferenceDataStoreFactory.create(
            produceFile = { context.preferencesDataStoreFile(DataStoreUtil.DATA_STORE_NAME) }
        )
}