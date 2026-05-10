package com.nikealarm.core.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.nikealarm.core.datastore.util.DataStoreUtil
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SettingDataSource @Inject constructor(
    private val settingDataStore: DataStore<Preferences>,
) {
    fun getAllowNotification(): Flow<Boolean> = settingDataStore.data
        .map { preference ->
            preference[DataStoreUtil.DATA_KEY_ALLOW_NOTIFICATION] ?: false
        }

    fun getAllowDrawNotification(): Flow<Boolean> = settingDataStore.data
        .map { preference ->
            preference[DataStoreUtil.DATA_KEY_ALLOW_DRAW_NOTIFICATION] ?: false
        }

    suspend fun setAllowNotification(isAllow: Boolean) {
        settingDataStore.edit { settings ->
            settings[DataStoreUtil.DATA_KEY_ALLOW_NOTIFICATION] = isAllow
        }
    }

    suspend fun setAllowDrawNotification(isAllow: Boolean) {
        settingDataStore.edit { settings ->
            settings[DataStoreUtil.DATA_KEY_ALLOW_DRAW_NOTIFICATION] = isAllow
        }
    }
}