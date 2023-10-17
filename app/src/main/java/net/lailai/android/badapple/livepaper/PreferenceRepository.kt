/**
 * Copyright (C) 2023 lailai.
 */
package net.lailai.android.badapple.livepaper

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * 設定用Repository
 * @author lailai
 */
class PreferenceRepository(context: Context) : SharedPreferences.OnSharedPreferenceChangeListener {
    private val _fps = MutableStateFlow("10")
    val fps: StateFlow<String> = _fps

    private val _aspect = MutableStateFlow("width")
    val aspect: StateFlow<String> = _aspect

    private val _direction = MutableStateFlow("portrait")
    val direction: StateFlow<String> = _direction

    init {
        context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE).apply {
            registerOnSharedPreferenceChangeListener(this@PreferenceRepository)
        }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        when (key) {
            PREF_KEY_FPS -> {
                _fps.value = (sharedPreferences?.getString(key, "10") ?: "10")
            }

            PREF_KEY_ASPECT -> {
                _aspect.value = sharedPreferences?.getString(key, "width") ?: "width"
            }

            PREF_KEY_DIRECTION -> {
                _direction.value = sharedPreferences?.getString(key, "portrait") ?: "portrait"
            }
        }
    }

    companion object {
        // 共有設定名
        const val SHARED_PREFS_NAME = "BadAppleSettings"

        // 共有設定キー(fps)
        private const val PREF_KEY_FPS = "net.lailai.android.badapple.livepaper.fps"

        // 共有設定キー(アスペクト比)
        private const val PREF_KEY_ASPECT = "net.lailai.android.badapple.livepaper.aspect"

        // 共有設定キー(向き)
        private const val PREF_KEY_DIRECTION = "net.lailai.android.badapple.livepaper.direction"
    }
}
