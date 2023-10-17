/**
 * Copyright (C) 2023 lailai.
 */
package net.lailai.android.badapple.livepaper

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * 設定画面のViewModel
 * @author lailai
 */
@HiltViewModel
class LiveWallpaperSettingsViewModel @Inject constructor(
    private val pfRepository: PreferenceRepository
) : ViewModel()
