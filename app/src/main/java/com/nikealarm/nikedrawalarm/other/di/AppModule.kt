package com.nikealarm.nikedrawalarm.other.di

import android.content.Context
import com.nikealarm.nikedrawalarm.other.Contents
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Named
import javax.inject.Singleton

@InstallIn(ApplicationComponent::class)
@Module
object AppModule {

    @Singleton
    @Provides
    @Named(Contents.PREFERENCE_NAME_TIME)
    fun provideTimePreferences(@ApplicationContext context: Context) =
        context.getSharedPreferences(
            Contents.PREFERENCE_NAME_TIME,
            Context.MODE_PRIVATE
        )

    @Singleton
    @Provides
    @Named(Contents.PREFERENCE_NAME_ALLOW_ALARM)
    fun provideAllowAlarmPreferences(@ApplicationContext context: Context) =
        context.getSharedPreferences(
            Contents.PREFERENCE_NAME_ALLOW_ALARM,
            Context.MODE_PRIVATE
        )
}