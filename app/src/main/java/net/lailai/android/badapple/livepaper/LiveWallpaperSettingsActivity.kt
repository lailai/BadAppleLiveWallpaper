/**
 * Copyright (C) 2020-2023 lailai.
 */
package net.lailai.android.badapple.livepaper

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit

/**
 * ライブ壁紙設定Activity
 * @author lailai
 */
class LiveWallpaperSettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_live_wallpaper_settings)
        supportFragmentManager.commit {
            replace(R.id.fragment_container_view, LiveWallpaperSettingsFragment())
        }
    }
}
