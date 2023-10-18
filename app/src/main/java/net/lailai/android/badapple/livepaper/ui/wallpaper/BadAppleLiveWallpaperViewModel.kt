/**
 * Copyright (C) 2023 lailai.
 */
package net.lailai.android.badapple.livepaper.ui.wallpaper

import kotlinx.coroutines.flow.map
import net.lailai.android.badapple.livepaper.data.PreferenceRepository

/**
 * ライブ壁紙のViewModel
 * @author lailai
 */
class BadAppleLiveWallpaperViewModel(private val pfRepository: PreferenceRepository) {
    /** 1秒あたりの描画枚数 */
    val fps = pfRepository.fps.map {
        try {
            it.toInt()
        } catch (e: NumberFormatException) {
            10
        }
    }

    /** アスペクト比 */
    val aspect = pfRepository.aspect.map {
        when (it) {
            "width" -> ASPECT_WIDTH
            "height" -> ASPECT_HEIGHT
            "full" -> ASPECT_FULL
            "fill" -> ASPECT_FILL
            else -> ASPECT_WIDTH
        }
    }

    /** 向き */
    val direction = pfRepository.direction.map {
        when (it) {
            "portrait" -> DIRECTION_PORTRAIT
            "landscape" -> DIRECTION_LANDSCAPE
            else -> DIRECTION_PORTRAIT
        }
    }

    companion object {
        /** アスペクト比設定値(横幅に合わせる) */
        const val ASPECT_WIDTH = 0

        /** アスペクト比設定値(縦幅に合わせる) */
        const val ASPECT_HEIGHT = 1

        /** アスペクト比設定値(全画面に合わせる) */
        const val ASPECT_FULL = 2

        /** アスペクト比設定値(表示画面に合わせる) */
        const val ASPECT_FILL = 3

        /** 向き設定値(縦) */
        const val DIRECTION_PORTRAIT = 0

        /** 向き設定値(横) */
        const val DIRECTION_LANDSCAPE = 1
    }
}
