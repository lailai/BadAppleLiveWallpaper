/**
 * Copyright (C) 2011-2014 lailai.
 */
package net.lailai.android.badapple.livepaper;

import java.io.IOException;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.view.SurfaceHolder;

/**
 * ライブ壁紙のメインクラス
 * @author lailai
 */
public class BadAppleLiveWallpaper extends WallpaperService {

    /** 共有設定名 */
    public static final String SHARED_PREFS_NAME = "BadAppleSettings";
    /** 使用する画像の幅 */
    public static final int IMG_WIDTH = 512;
    /** 使用する画像の高さ */
    public static final int IMG_HEIGHT = 384;
    /** 使用する画像の枚数 */
    public static final int IMG_COUNT = 6570;

    /* (非 Javadoc)
     * @see android.service.wallpaper.WallpaperService#onCreateEngine()
     */
    @Override
    public Engine onCreateEngine() {
        return new BadAppleLiveWallpaperEngine();
    }

    /**
     * ライブ壁紙のエンジン
     */
    class BadAppleLiveWallpaperEngine extends Engine implements SharedPreferences.OnSharedPreferenceChangeListener {

        /** ハンドラー */
        private final Handler mHandler = new Handler();
        /** 描画用スレッド */
        private final Runnable mDrawBadApple = new Runnable() {

            @Override
            public void run() {
                drawFrame();
            }

        };
        /** 共有設定 */
        private SharedPreferences mSharedPref;
        /** 画像ファイルアクセス */
        private Resources mResource;
        /** 画像読み込み */
        private Bitmap mBmp;
        /** 描画フラグ */
        private boolean mVisible;
        /** 画面横幅 */
        private int mWidth;
        /** 画面高さ */
        private int mHeight;
        /** ホーム画面スワイプ時のオフセット */
        private int mXPixelOffset;
        /** ホーム画面スワイプ時のオフセット */
        private int mYPixelOffset;
        /** 調整後の描画画像の横幅 */
        private int mFrameWidth;
        /** 調整後の描画画像の高さ */
        private int mFrameHeight;
        /** 描画中画像の番号 */
        private int mBmpCount;
        /** 開始時間(描画タイミング計測) */
        private long mStart;
        /** 描画フレーム取得時間(タイミング計測) */
        private long mMiddle;
        /** 終了時間(描画タイミング計測)*/
        private long mEnd;
        /** 遅延時間 */
        private long mDelay;
        /** 1秒あたりの描画枚数 */
        private int mFps;
        /** アスペクト比 */
        private String mAspect;
        /** 向き */
        private String mDirection;
        /** 回転行列 */
        private Matrix mMatrix;


        /**
         * コンストラクタ
         */
        public BadAppleLiveWallpaperEngine() {
            mResource = getResources();
            mSharedPref = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE);
            mSharedPref.registerOnSharedPreferenceChangeListener(this);
            mAspect = mSharedPref.getString("net.lailai.android.badapple.livepaper.aspect", "width");
            mDirection = mSharedPref.getString("net.lailai.android.badapple.livepaper.direction", "portrait");
            String fps = mSharedPref.getString("net.lailai.android.badapple.livepaper.fps", "10");
            mVisible = false;
            mXPixelOffset = 0;
            mYPixelOffset = 0;
            mBmpCount = 0;
            mBmp = null;
            mStart = -1;
            mFps = 10;
            mMatrix = new Matrix();
            mMatrix.postRotate(90.0f, 256.0f, 192.0f);
            try {
                mFps = Integer.valueOf(fps);
            } catch (NumberFormatException e) {
                // 数値以外を取得したら初期値を強制的に設定する
                mFps = 10;
                SharedPreferences.Editor editor = mSharedPref.edit();
                editor.putString("net.lailai.android.badapple.livepaper.fps", String.valueOf(mFps));
                editor.commit();
            }
            if (mFps <= 0) {
                mFps = 1;
                SharedPreferences.Editor editor = mSharedPref.edit();
                editor.putString("net.lailai.android.badapple.livepaper.fps", String.valueOf(mFps));
                editor.commit();
            } else if (mFps > 30) {
                mFps = 30;
                SharedPreferences.Editor editor = mSharedPref.edit();
                editor.putString("net.lailai.android.badapple.livepaper.fps", String.valueOf(mFps));
                editor.commit();
            }
        }

        /* (非 Javadoc)
         * @see android.service.wallpaper.WallpaperService.Engine#onSurfaceChanged(android.view.SurfaceHolder, int, int, int)
         */
        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            super.onSurfaceChanged(holder, format, width, height);
            // 画面サイズを取得して描画
            mWidth = width;
            mHeight = height;
            drawFrame();
        }

        /* (非 Javadoc)
         * @see android.service.wallpaper.WallpaperService.Engine#onSurfaceDestroyed(android.view.SurfaceHolder)
         */
        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            super.onSurfaceDestroyed(holder);
            // 次の描画予約をキャンセル
            mVisible = false;
            mHandler.removeCallbacks(mDrawBadApple);
        }

        /* (非 Javadoc)
         * @see android.service.wallpaper.WallpaperService.Engine#onVisibilityChanged(boolean)
         */
        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);
            mVisible = visible;
            if (visible) {
                // 描画
                drawFrame();
            } else {
                // 描画キャンセル
                mHandler.removeCallbacks(mDrawBadApple);
            }
        }

        /* (非 Javadoc)
         * @see android.service.wallpaper.WallpaperService.Engine#onOffsetsChanged(float, float, float, float, int, int)
         */
        @Override
        public void onOffsetsChanged(float xOffset, float yOffset, float xOffsetStep, float yOffsetStep, int xPixelOffset, int yPixelOffset) {
            super.onOffsetsChanged(xOffset, yOffset, xOffsetStep, yOffsetStep, xPixelOffset, yPixelOffset);
            mXPixelOffset = xPixelOffset;
            mYPixelOffset = yPixelOffset;
        }

        /* (非 Javadoc)
         * @see android.content.SharedPreferences.OnSharedPreferenceChangeListener#onSharedPreferenceChanged(android.content.SharedPreferences, java.lang.String)
         */
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            mAspect = sharedPreferences.getString("net.lailai.android.badapple.livepaper.aspect", "width");
            mDirection = sharedPreferences.getString("net.lailai.android.badapple.livepaper.direction", "portrait");
            String fps = sharedPreferences.getString("net.lailai.android.badapple.livepaper.fps", "10");
            try {
                mFps = Integer.valueOf(fps);
            } catch (NumberFormatException e) {
                // 数値以外を取得したら初期値を強制的に設定する
                mFps = 10;
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("net.lailai.android.badapple.livepaper.fps", String.valueOf(mFps));
                editor.commit();
            }
            if (mFps <= 0) {
                mFps = 1;
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("net.lailai.android.badapple.livepaper.fps", String.valueOf(mFps));
                editor.commit();
            } else if (mFps > 30) {
                mFps = 30;
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("net.lailai.android.badapple.livepaper.fps", String.valueOf(mFps));
                editor.commit();
            }
        }

        /**
         * BadApple影絵の画像を描画します。
         */
        void drawFrame() {
            if (mStart == -1) {
                mStart = System.currentTimeMillis();
            }
            final SurfaceHolder holder = getSurfaceHolder();

            Canvas canvas = null;
            try {
                canvas = holder.lockCanvas();
                if (canvas != null) {
                    // 描画フレーム番号の算出
                    mMiddle = System.currentTimeMillis();
                    mBmpCount = (int) (((mMiddle - mStart) * 30 / 1000) % IMG_COUNT);
                    String imgNumber = String.format("%1$04d", mBmpCount);
                    // 画像取得
                    mBmp = BitmapFactory.decodeStream(mResource.getAssets().open("BadAppleImg/BadApple_" + imgNumber +".jpg"));

                    if (mDirection.equals("portrait")) {
                        // 描画
                        if (mAspect.equals("full")) {
                            mFrameWidth = 2 * mWidth;
                            mFrameHeight = mHeight;
                            canvas.drawColor(Color.BLACK);
                            canvas.drawBitmap(mBmp, new Rect((int) (-1.0 * mXPixelOffset * mBmp.getWidth() / mFrameWidth), 0, mBmp.getWidth(), mBmp.getHeight()), new Rect(0, 0, mFrameWidth + mXPixelOffset, mFrameHeight), null);
                        } else if (mAspect.equals("fill")) {
                            canvas.drawBitmap(mBmp, null, new Rect(0, 0, mWidth, mHeight), null);
                        } else if (mAspect.equals("width")) {
                            mFrameWidth = mWidth;
                            mFrameHeight = mWidth * 384 / 512;
                            canvas.drawColor(Color.BLACK);
                            canvas.drawBitmap(mBmp, null, new Rect(0, (mHeight - mFrameHeight) / 2, mFrameWidth, mFrameHeight + ((mHeight - mFrameHeight) / 2)), null);
                        } else if (mAspect.equals("height")) {
                            mFrameWidth = mHeight * 512 / 384;
                            mFrameHeight = mHeight;
                            canvas.drawColor(Color.BLACK);
                            canvas.drawBitmap(mBmp, null, new Rect((mWidth - mFrameWidth) / 2, 0, mFrameWidth + ((mWidth - mFrameWidth) / 2), mFrameHeight), null);
                        }
                    } else {
                        // 回転
                        mBmp = Bitmap.createBitmap(mBmp, 0, 0, 512, 384, mMatrix, true);
                        // 描画
                        if (mAspect.equals("full")) {
                            mFrameWidth = 2 * mWidth;
                            mFrameHeight = mHeight;
                            canvas.drawColor(Color.BLACK);
                            canvas.drawBitmap(mBmp, new Rect((int) (-1.0 * mXPixelOffset * mBmp.getWidth() / mFrameWidth), 0, mBmp.getWidth(), mBmp.getHeight()), new Rect(0, 0, mFrameWidth + mXPixelOffset, mFrameHeight), null);
                        } else if (mAspect.equals("fill")) {
                            canvas.drawBitmap(mBmp, null, new Rect(0, 0, mWidth, mHeight), null);
                        } else if (mAspect.equals("width")) {
                            mFrameWidth = mWidth;
                            mFrameHeight = mWidth * 512 / 384;
                            canvas.drawColor(Color.BLACK);
                            canvas.drawBitmap(mBmp, null, new Rect(0, (mHeight - mFrameHeight) / 2, mFrameWidth, mFrameHeight + ((mHeight - mFrameHeight) / 2)), null);
                        } else if (mAspect.equals("height")) {
                            mFrameWidth = mHeight * 384 / 512;
                            mFrameHeight = mHeight;
                            canvas.drawColor(Color.BLACK);
                            canvas.drawBitmap(mBmp, null, new Rect((mWidth - mFrameWidth) / 2, 0, mFrameWidth + ((mWidth - mFrameWidth) / 2), mFrameHeight), null);
                        }

                    }
                }
            } catch (IOException e) {
                // nothing to do
            } finally {
                if (canvas != null) {
                    holder.unlockCanvasAndPost(canvas);
                }
            }
            mHandler.removeCallbacks(mDrawBadApple);
            if (mVisible) {
                // ディレイの設定
                mEnd = System.currentTimeMillis();
                mDelay = (1000 / mFps) - (mEnd - mStart) % (1000 / mFps);
                // システム負荷の観点から最低10msの余裕を持たせる
                // 周回遅れ？気にしない
                if (mDelay < 10) {
                    mDelay = 10;
                }
                mHandler.postDelayed(mDrawBadApple, mDelay);
            }
        }

    }

}
