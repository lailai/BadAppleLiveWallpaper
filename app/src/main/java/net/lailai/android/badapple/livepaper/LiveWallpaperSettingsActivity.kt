/**
 * Copyright (C) 2020-2024 lailai.
 */
package net.lailai.android.badapple.livepaper

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import net.lailai.android.badapple.livepaper.ui.App
import net.lailai.android.badapple.livepaper.ui.theme.BadAppleLiveWallpaperTheme

/**
 * ライブ壁紙設定Activity
 * @author lailai
 */
class LiveWallpaperSettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BadAppleLiveWallpaperTheme {
                App(onBack = { finish() })
            }
        }
    }
}
