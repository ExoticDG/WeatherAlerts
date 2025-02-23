package com.exoticdg.weatheralerts.ui.settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.exoticdg.weatheralerts.R

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }
}