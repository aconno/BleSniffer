package com.aconno.blesniffer.preferences

import android.content.Context
import androidx.preference.PreferenceManager

class BleSnifferPreferences(val context : Context) {
    private val sharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(context)
    }

    fun isKeepScreenOn() : Boolean {
        return sharedPreferences.getBoolean(KEEP_SCREEN_ON,false)
    }

    companion object {
        const val KEEP_SCREEN_ON = "keep_screen_on"
        const val SHOW_ONLY_MANUFACTURER_DATA = "show_only_manufacturer_data"
        const val ADVERTISEMENT_BYTES_DISPLAY_MODE = "adv_bytes_display_mode"
        const val ADVANCED_MODE = "advanced_mode"
    }
}