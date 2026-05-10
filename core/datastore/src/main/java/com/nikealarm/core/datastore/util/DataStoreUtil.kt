package com.nikealarm.core.datastore.util

import androidx.datastore.preferences.core.booleanPreferencesKey

internal object DataStoreUtil {
    const val DATA_STORE_NAME = "Settings.preferences_pb"
    val DATA_KEY_ALLOW_NOTIFICATION = booleanPreferencesKey("DATA_KEY_ALLOW_NOTIFICATION")
    val DATA_KEY_ALLOW_DRAW_NOTIFICATION = booleanPreferencesKey("DATA_KEY_ALLOW_DRAW_NOTIFICATION")
}