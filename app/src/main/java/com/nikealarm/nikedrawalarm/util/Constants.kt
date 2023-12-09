package com.nikealarm.nikedrawalarm.util

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey

object Constants {
    const val LOCALIZING =32400000L

    const val DEVELOPER_EMAIL = "sghoregoodeveloper@gmail.com"

    const val NIKE_API_URL = "https://api.nike.com/product_feed/threads/v3/"
    const val NIKE_PRODUCT_URL = "https://www.nike.com/kr/launch/t/"

    const val PRODUCT_DETAIL_URI = "https:com.nikealarm.nikedrawalarm/product"

    const val INTENT_ACTION_PRODUCT_NOTIFICATION = "INTENT_ACTION_PRODUCT_NOTIFICATION"
    const val INTENT_ACTION_NEW_DRAW_PRODUCT_NOTIFICATION =
        "INTENT_ACTION_NEW_DRAW_PRODUCT_NOTIFICATION"
    const val INTENT_PRODUCT_ID = "INTENT_PRODUCT_ID"

    const val REQUEST_CODE_REPEAT_ALARM = 100

    const val CHANNEL_ID_PRODUCT_NOTIFICATION = "CHANNEL_ID_PRODUCT_NOTIFICATION"
    const val CHANNEL_ID_DRAW_NEW_PRODUCT_NOTIFICATION = "CHANNEL_ID_DRAW_NEW_PRODUCT_NOTIFICATION"

    const val DATA_STORE_NAME = "Settings.preferences_pb"
    val DATA_KEY_ALLOW_NOTIFICATION = booleanPreferencesKey("DATA_KEY_ALLOW_NOTIFICATION")
    val DATA_KEY_ALLOW_DRAW_NOTIFICATION = booleanPreferencesKey("DATA_KEY_ALLOW_DRAW_NOTIFICATION")
}