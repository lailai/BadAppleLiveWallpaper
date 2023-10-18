/**
 * Copyright (C) 2020-2023 lailai.
 */
package net.lailai.android.badapple.livepaper.ui.settings

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.preference.PreferenceFragmentCompat
import dagger.hilt.android.AndroidEntryPoint
import net.lailai.android.badapple.livepaper.R
import net.lailai.android.badapple.livepaper.data.PreferenceRepository

/**
 * ライブ壁紙設定Fragment
 * @author lailai
 */
@AndroidEntryPoint
class LiveWallpaperSettingsFragment : PreferenceFragmentCompat() {
    private val vm: LiveWallpaperSettingsViewModel by viewModels()

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceManager.sharedPreferencesName = PreferenceRepository.SHARED_PREFS_NAME
        setPreferencesFromResource(R.xml.preference_wallpaper, rootKey)
    }
}
