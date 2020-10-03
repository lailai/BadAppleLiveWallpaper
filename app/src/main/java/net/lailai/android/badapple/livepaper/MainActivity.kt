/**
 * Copyright (C) 2011-2020 lailai.
 */
package net.lailai.android.badapple.livepaper

import android.app.WallpaperManager
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

/**
 * ライブ壁紙設定画面に飛ばすだけのActivity
 */
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER).apply {
            putExtra(
                WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                ComponentName(this@MainActivity, BadAppleLiveWallpaperService::class.java)
            )
        }
        startActivity(intent)
        finish()
    }
}
