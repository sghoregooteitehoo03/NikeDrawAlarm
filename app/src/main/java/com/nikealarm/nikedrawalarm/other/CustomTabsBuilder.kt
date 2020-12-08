package com.nikealarm.nikedrawalarm.other

import android.graphics.Color
import androidx.browser.customtabs.CustomTabsIntent

class CustomTabsBuilder {

    fun getBuilder(): CustomTabsIntent.Builder {
        return CustomTabsIntent.Builder().apply {
            setToolbarColor(Color.BLACK)
        }
    }
}