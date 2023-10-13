/**
 * Copyright (C) 2020-2023 lailai.
 */
package net.lailai.android.badapple.livepaper

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat

/**
 * ライブ壁紙設定Fragment
 * @author lailai
 */
class LiveWallpaperSettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceManager.sharedPreferencesName = Constants.SHARED_PREFS_NAME
        setPreferencesFromResource(R.xml.preference_wallpaper, rootKey)
    }
}
