/**
 * Copyright (C) 2011-2023 lailai.
 */
package net.lailai.android.badapple.livepaper

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
import dagger.hilt.EntryPoint
import dagger.hilt.EntryPoints
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.IOException
import kotlin.math.max

/**
 * ライブ壁紙のメインクラス
 * @author lailai
 */
class BadAppleLiveWallpaperService : WallpaperService() {
    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface ViewModelInterface {
        fun getViewModel(): BadAppleLiveWallpaperViewModel
    }

    override fun onCreateEngine(): Engine = BadAppleLiveWallpaperEngine()

    // ライブ壁紙エンジン
    private inner class BadAppleLiveWallpaperEngine : Engine() {
        // CoroutineScope
        private var coroutineScope: CoroutineScope? = null

        // ViewModel
        private val vm =
            EntryPoints.get(applicationContext, ViewModelInterface::class.java).getViewModel()

        // ハンドラー
        private val handler = Handler(Looper.myLooper()!!)

        // 描画用スレッド
        private val drawRunnable = Runnable { drawFrame() }

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

        // 回転行列
        private val rotateMatrix = Matrix().apply { postRotate(90.0f, 256.0f, 192.0f) }

        override fun onSurfaceCreated(holder: SurfaceHolder?) {
            super.onSurfaceCreated(holder)
            coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
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
            coroutineScope?.cancel()
            coroutineScope = null
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

        // フレームを描画する
        private fun drawFrame() {
            coroutineScope?.launch {
                val startDrawTime = System.currentTimeMillis()

                // 画像取得
                val index = (((startDrawTime - start) / (1000 / 30)) % IMG_MAX_INDEX).toInt()
                val bitmapIndex = String.format("%1$04d", index)
                val fps = vm.fps.first()
                val bitmap = try {
                    resources.assets.open("bad_apple_img/bad_apple_$bitmapIndex.jpg").use {
                        BitmapFactory.decodeStream(it)
                    }
                } catch (e: IOException) {
                    val endDrawTime = System.currentTimeMillis()
                    val delay = max(1000L / fps - (endDrawTime - startDrawTime), 10L)
                    handler.removeCallbacks(drawRunnable)
                    handler.postDelayed(drawRunnable, delay)
                    return@launch
                }

                // 画像描画
                surfaceHolder.lockCanvas()?.let {
                    drawBitmap(bitmap, it)
                    surfaceHolder.unlockCanvasAndPost(it)
                }

                val endDrawTime = System.currentTimeMillis()
                val delay = max(1000L / fps - (endDrawTime - startDrawTime), 10L)
                handler.removeCallbacks(drawRunnable)
                handler.postDelayed(drawRunnable, delay)
            }
        }

        // 画像を描画する
        private suspend fun drawBitmap(bitmap: Bitmap, canvas: Canvas) {
            val direction = vm.direction.first()
            val aspect = vm.aspect.first()
            when (direction) {
                BadAppleLiveWallpaperViewModel.DIRECTION_PORTRAIT -> {
                    when (aspect) {
                        BadAppleLiveWallpaperViewModel.ASPECT_WIDTH -> {
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

                        BadAppleLiveWallpaperViewModel.ASPECT_HEIGHT -> {
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

                        BadAppleLiveWallpaperViewModel.ASPECT_FULL -> {
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

                        BadAppleLiveWallpaperViewModel.ASPECT_FILL -> {
                            canvas.drawBitmap(bitmap, null, Rect(0, 0, width, height), null)
                        }
                    }
                }

                BadAppleLiveWallpaperViewModel.DIRECTION_LANDSCAPE -> {
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
                        BadAppleLiveWallpaperViewModel.ASPECT_WIDTH -> {
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

                        BadAppleLiveWallpaperViewModel.ASPECT_HEIGHT -> {
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

                        BadAppleLiveWallpaperViewModel.ASPECT_FULL -> {
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

                        BadAppleLiveWallpaperViewModel.ASPECT_FILL -> {
                            canvas.drawBitmap(rotateBitmap, null, Rect(0, 0, width, height), null)
                        }
                    }
                }
            }
        }
    }

    companion object {
        // 使用する画像の最大要素
        private const val IMG_MAX_INDEX = 6574
    }
}
