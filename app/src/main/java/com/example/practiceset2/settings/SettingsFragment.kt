package com.example.practiceset2.settings

import android.content.SharedPreferences
import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.example.practiceset2.activities.MainActivity
import com.example.practiceset2.R

class SettingsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }

    override fun onSharedPreferenceChanged(sharedPrefences: SharedPreferences?, key: String?) {
        if (key == "colorMode"){
            val isLight = sharedPrefences?.getBoolean("colorMode", true)!!
            val activityMain = activity as MainActivity
            activityMain.setAppTheme(activityMain, if (isLight) R.style.Theme_PracticeSet2 else R.style.Theme_PracticeSet2Night)
        }
    }

    override fun onResume() {
        super.onResume()
        preferenceScreen.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        preferenceScreen.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }



}