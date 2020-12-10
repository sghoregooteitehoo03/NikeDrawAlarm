package com.nikealarm.nikedrawalarm.other.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.nikealarm.nikedrawalarm.database.Dao
import com.nikealarm.nikedrawalarm.database.MyDataBase
import com.nikealarm.nikedrawalarm.other.Contents
import com.nikealarm.nikedrawalarm.viewmodel.MyRepository
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

    @Singleton
    @Provides
    @Named(Contents.PREFERENCE_NAME_AUTO_ENTER)
    fun provideAutoEnterPreferences(@ApplicationContext context: Context) =
        context.getSharedPreferences(
            Contents.PREFERENCE_NAME_AUTO_ENTER,
            Context.MODE_PRIVATE
        )

    @Singleton
    @Provides
    @Named(Contents.PREFERENCE_NAME_AUTO_ENTER_V2)
    fun provideAutoEnterPreferencesV2(@ApplicationContext context: Context): SharedPreferences {
        val mainKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        return EncryptedSharedPreferences.create(
            context,
            Contents.PREFERENCE_NAME_AUTO_ENTER,
            mainKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    @Singleton
    @Provides
    @Named(Contents.PREFERENCE_NAME_UPDATE)
    fun provideUpdatePreferences(@ApplicationContext context: Context) =
        context.getSharedPreferences(
            Contents.PREFERENCE_NAME_UPDATE,
            Context.MODE_PRIVATE
        )

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context) =
        Room.databaseBuilder(
            context,
            MyDataBase::class.java,
            "database"
        )
            .fallbackToDestructiveMigration()
            .build()

    @Singleton
    @Provides
    fun provideDao(database: MyDataBase) =
        database.getDao()

    @Singleton
    @Provides
    fun provideRepository(dao: Dao) =
        MyRepository(dao)
}