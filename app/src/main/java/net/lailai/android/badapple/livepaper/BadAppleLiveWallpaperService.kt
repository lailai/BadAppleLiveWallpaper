/**
 * Copyright (C) 2011-2020 lailai.
 */
package net.lailai.android.badapple.livepaper

import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Rect
import android.os.Handler
import android.os.Looper
import android.service.wallpaper.WallpaperService
import android.view.SurfaceHolder
import androidx.core.content.edit
import java.io.IOException
import java.lang.Long.max

/**
 * ライブ壁紙のメインクラス
 * @author lailai
 */
class BadAppleLiveWallpaperService : WallpaperService() {
    override fun onCreateEngine(): Engine {
        return BadAppleLiveWallpaperEngine()
    }

    // ライブ壁紙エンジン
    private inner class BadAppleLiveWallpaperEngine :
        Engine(), SharedPreferences.OnSharedPreferenceChangeListener {
        // ハンドラー
        private val handler = Handler(Looper.myLooper()!!)

        // 描画用スレッド
        private val drawRunnable = Runnable { drawFrame() }

        // 共有設定
        private val sharedPreferences =
            getSharedPreferences(Constants.SHARED_PREFS_NAME, MODE_PRIVATE).apply {
                registerOnSharedPreferenceChangeListener(this@BadAppleLiveWallpaperEngine)
            }

        // 画面横幅
        private var width = 0

        // 画面縦幅
        private var height = 0

        // ホーム画面スワイプ時のオフセット
        private var xPixelOffset = 0

        // ホーム画面スワイプ時のオフセット
        private var yPixelOffset = 0

        // 開始時間
        private var start = 0L

        // 1秒あたりの描画枚数
        private var fps = getFps()

        // アスペクト比
        private var aspect = getAspect()

        // 向き
        private var direction = getDirection()

        // 回転行列
        private val rotateMatrix = Matrix().apply { postRotate(90.0f, 256.0f, 192.0f) }

        override fun onSurfaceCreated(holder: SurfaceHolder?) {
            super.onSurfaceCreated(holder)
            start = System.currentTimeMillis()
        }

        override fun onSurfaceChanged(
            holder: SurfaceHolder?,
            format: Int,
            width: Int,
            height: Int
        ) {
            super.onSurfaceChanged(holder, format, width, height)
            this.width = width
            this.height = height
            drawFrame()
        }

        override fun onSurfaceDestroyed(holder: SurfaceHolder?) {
            super.onSurfaceDestroyed(holder)
            handler.removeCallbacks(drawRunnable)
        }

        override fun onVisibilityChanged(visible: Boolean) {
            super.onVisibilityChanged(visible)
            if (visible) {
                drawFrame()
            } else {
                handler.removeCallbacks(drawRunnable)
            }
        }

        override fun onOffsetsChanged(
            xOffset: Float,
            yOffset: Float,
            xOffsetStep: Float,
            yOffsetStep: Float,
            xPixelOffset: Int,
            yPixelOffset: Int
        ) {
            super.onOffsetsChanged(
                xOffset,
                yOffset,
                xOffsetStep,
                yOffsetStep,
                xPixelOffset,
                yPixelOffset
            )
            this.xPixelOffset = xPixelOffset
            this.yPixelOffset = yPixelOffset
        }

        override fun onSharedPreferenceChanged(
            sharedPreferences: SharedPreferences?,
            key: String?
        ) {
            fps = getFps()
            aspect = getAspect()
            direction = getDirection()
        }

        // フレームを描画する
        private fun drawFrame() {
            val startDrawTime = System.currentTimeMillis()

            // 画像取得
            val index = (((startDrawTime - start) / (1000 / 30)) % IMG_MAX_INDEX).toInt()
            val bitmapIndex = String.format("%1$04d", index)
            val bitmap = try {
                resources.assets.open("bad_apple_img/bad_apple_$bitmapIndex.jpg").use {
                    BitmapFactory.decodeStream(it)
                }
            } catch (e: IOException) {
                val endDrawTime = System.currentTimeMillis()
                val delay = max(1000L / fps - (endDrawTime - startDrawTime), 10L)
                handler.removeCallbacks(drawRunnable)
                handler.postDelayed(drawRunnable, delay)
                return
            }

            // 画像描画
            val canvas = surfaceHolder.lockCanvas()
            drawBitmap(bitmap, canvas)
            surfaceHolder.unlockCanvasAndPost(canvas)

            val endDrawTime = System.currentTimeMillis()
            val delay = max(1000L / fps - (endDrawTime - startDrawTime), 10L)
            handler.removeCallbacks(drawRunnable)
            handler.postDelayed(drawRunnable, delay)
        }

        // 画像を描画する
        private fun drawBitmap(bitmap: Bitmap, canvas: Canvas) {
            when (direction) {
                DIRECTION_PORTRAIT -> {
                    when (aspect) {
                        ASPECT_WIDTH -> {
                            val frameWidth = width
                            val frameHeight = width * bitmap.height / bitmap.width
                            canvas.drawColor(Color.BLACK)
                            canvas.drawBitmap(
                                bitmap,
                                null,
                                Rect(
                                    0,
                                    (height - frameHeight) / 2,
                                    frameWidth,
                                    (height - frameHeight) / 2 + frameHeight
                                ),
                                null
                            )
                        }
                        ASPECT_HEIGHT -> {
                            val frameWidth = height * bitmap.width / bitmap.height
                            val frameHeight = height
                            canvas.drawColor(Color.BLACK)
                            canvas.drawBitmap(
                                bitmap,
                                null,
                                Rect(
                                    (width - frameWidth) / 2,
                                    0,
                                    (width - frameWidth) / 2 + frameWidth,
                                    frameHeight
                                ),
                                null
                            )
                        }
                        ASPECT_FULL -> {
                            val frameWidth = width * 2
                            val frameHeight = height
                            canvas.drawColor(Color.BLACK)
                            canvas.drawBitmap(
                                bitmap,
                                Rect(
                                    (-1.0 * xPixelOffset * bitmap.width / frameWidth).toInt(),
                                    0,
                                    bitmap.width,
                                    bitmap.height
                                ),
                                Rect(0, 0, frameWidth + xPixelOffset, frameHeight),
                                null
                            )
                        }
                        ASPECT_FILL -> {
                            canvas.drawBitmap(bitmap, null, Rect(0, 0, width, height), null)
                        }
                    }
                }
                DIRECTION_LANDSCAPE -> {
                    val rotateBitmap = Bitmap.createBitmap(
                        bitmap,
                        0,
                        0,
                        bitmap.width,
                        bitmap.height,
                        rotateMatrix,
                        true
                    )
                    when (aspect) {
                        ASPECT_WIDTH -> {
                            val frameWidth = width
                            val frameHeight = width * rotateBitmap.height / rotateBitmap.width
                            canvas.drawColor(Color.BLACK)
                            canvas.drawBitmap(
                                rotateBitmap,
                                null,
                                Rect(
                                    0,
                                    (height - frameHeight) / 2,
                                    frameWidth,
                                    (height - frameHeight) / 2 + frameHeight
                                ),
                                null
                            )
                        }
                        ASPECT_HEIGHT -> {
                            val frameWidth = height * rotateBitmap.width / rotateBitmap.height
                            val frameHeight = height
                            canvas.drawColor(Color.BLACK)
                            canvas.drawBitmap(
                                rotateBitmap,
                                null,
                                Rect(
                                    (width - frameWidth) / 2,
                                    0,
                                    (width - frameWidth) / 2 + frameWidth,
                                    frameHeight
                                ),
                                null
                            )
                        }
                        ASPECT_FULL -> {
                            val frameWidth = width * 2
                            val frameHeight = height
                            canvas.drawColor(Color.BLACK)
                            canvas.drawBitmap(
                                rotateBitmap,
                                Rect(
                                    (-1.0 * xPixelOffset * rotateBitmap.width / frameWidth).toInt(),
                                    0,
                                    rotateBitmap.width,
                                    rotateBitmap.height
                                ),
                                Rect(0, 0, frameWidth + xPixelOffset, frameHeight),
                                null
                            )
                        }
                        ASPECT_FILL -> {
                            canvas.drawBitmap(rotateBitmap, null, Rect(0, 0, width, height), null)
                        }
                    }
                }
            }
        }

        // 共有設定からfpsを取得する
        private fun getFps(): Int {
            val fps = try {
                sharedPreferences.getString(PREF_KEY_FPS, "10")?.toInt() ?: 10
            } catch (e: NumberFormatException) {
                sharedPreferences.edit(true) { putString(PREF_KEY_FPS, "10") }
                10
            }
            return if (fps <= 0) {
                sharedPreferences.edit(true) { putString(PREF_KEY_FPS, "1") }
                1
            } else if (fps > 30) {
                sharedPreferences.edit(true) { putString(PREF_KEY_FPS, "30") }
                30
            } else {
                fps
            }
        }

        // 共有設定からアスペクト比を取得する
        private fun getAspect(): Int {
            return sharedPreferences.getString(PREF_KEY_ASPECT, "width")?.let {
                when (it) {
                    "width" -> ASPECT_WIDTH
                    "height" -> ASPECT_HEIGHT
                    "full" -> ASPECT_FULL
                    "fill" -> ASPECT_FILL
                    else -> ASPECT_WIDTH
                }
            } ?: ASPECT_WIDTH
        }

        // 共有設定から向きを取得する
        private fun getDirection(): Int {
            return sharedPreferences.getString(PREF_KEY_DIRECTION, "portrait")?.let {
                when (it) {
                    "portrait" -> DIRECTION_PORTRAIT
                    "landscape" -> DIRECTION_LANDSCAPE
                    else -> DIRECTION_PORTRAIT
                }
            } ?: DIRECTION_PORTRAIT
        }
    }

    companion object {
        // 使用する画像の最大要素
        private const val IMG_MAX_INDEX = 6574

        // アスペクト比設定値(横幅に合わせる)
        private const val ASPECT_WIDTH = 0

        // アスペクト比設定値(縦幅に合わせる)
        private const val ASPECT_HEIGHT = 1

        // アスペクト比設定値(全画面に合わせる)
        private const val ASPECT_FULL = 2

        // アスペクト比設定値(表示画面に合わせる)
        private const val ASPECT_FILL = 3

        // 向き設定値(縦)
        private const val DIRECTION_PORTRAIT = 0

        // 向き設定値(横)
        private const val DIRECTION_LANDSCAPE = 1

        // 共有設定キー(fps)
        private const val PREF_KEY_FPS = "net.lailai.android.badapple.livepaper.fps"

        // 共有設定キー(アスペクト比)
        private const val PREF_KEY_ASPECT = "net.lailai.android.badapple.livepaper.aspect"

        // 共有設定キー(向き)
        private const val PREF_KEY_DIRECTION = "net.lailai.android.badapple.livepaper.direction"
    }
}
