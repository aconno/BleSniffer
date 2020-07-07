package com.aconno.blesniffer.preferences

import android.content.Context
import androidx.preference.PreferenceManager
import com.aconno.blesniffer.domain.byteformatter.ByteArrayFormatMode

class BleSnifferPreferences(val context : Context) {
    private val sharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(context)
    }

    fun isKeepScreenOn() : Boolean {
        return sharedPreferences.getBoolean(KEEP_SCREEN_ON,false)
    }

    fun getAdvertisementBytesDisplayMode() : ByteArrayFormatMode {
        val displayMode = sharedPreferences.getString(ADVERTISEMENT_BYTES_DISPLAY_MODE,"0")

        //this conversion has to be consistent with adv_bytes_display_mode and adv_bytes_display_mode_values string arrays
        return when(displayMode?.toInt()) {
            0 -> ByteArrayFormatMode.SINGLE_BYTE_WITH_PREFIX
            1 -> ByteArrayFormatMode.SINGLE_BYTE
            2 -> ByteArrayFormatMode.BYTE_PAIRS
            else -> ByteArrayFormatMode.SINGLE_BYTE_WITH_PREFIX
        }
    }

    fun isShowOnlyManufacturerData() : Boolean {
        return sharedPreferences.getBoolean(SHOW_ONLY_MANUFACTURER_DATA,false)
    }

    companion object {
        const val KEEP_SCREEN_ON = "keep_screen_on"
        const val SHOW_ONLY_MANUFACTURER_DATA = "show_only_manufacturer_data"
        const val ADVERTISEMENT_BYTES_DISPLAY_MODE = "adv_bytes_display_mode"
        const val ADVANCED_MODE = "advanced_mode"
    }
}