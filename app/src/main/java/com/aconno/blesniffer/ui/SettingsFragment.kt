package com.aconno.blesniffer.ui

import android.os.Bundle
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.aconno.blesniffer.R

class SettingsFragment : PreferenceFragmentCompat(), Preference.OnPreferenceChangeListener {

    private var advBytesDisplayModePref : ListPreference? = null

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
        advBytesDisplayModePref = findPreference(ADV_BYTES_DISPLAY_MODE_KEY) as ListPreference?

        val advBytesDisplayMode = preferenceManager.sharedPreferences.getString(ADV_BYTES_DISPLAY_MODE_KEY,
            ADV_BYTES_DISPLAY_MODE_DEFAULT_VALUE)
        setAdvBytesDisplayModeSummary(advBytesDisplayMode)
    }

    override fun onResume() {
        super.onResume()
        advBytesDisplayModePref?.onPreferenceChangeListener = this
    }

    override fun onPause() {
        super.onPause()
        advBytesDisplayModePref?.onPreferenceChangeListener = null
    }

    private fun setAdvBytesDisplayModeSummary(selectedDisplayMode : String?) {
        advBytesDisplayModePref?.summary = resources.getStringArray(R.array.adv_bytes_display_mode)[selectedDisplayMode?.toInt() ?: 0]
    }

    override fun onPreferenceChange(preference: Preference?, newValue: Any?): Boolean {
        when(preference?.key) {
            ADV_BYTES_DISPLAY_MODE_KEY -> setAdvBytesDisplayModeSummary(newValue as String)
            else -> return false
        }
        return true
    }

    companion object {
        const val ADV_BYTES_DISPLAY_MODE_KEY = "adv_bytes_display_mode"
        const val ADV_BYTES_DISPLAY_MODE_DEFAULT_VALUE = "1"
    }

}