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
 * ���C�u�ǎ��̃��C���N���X
 * @author lailai
 */
public class BadAppleLiveWallpaper extends WallpaperService {

    /** ���L�ݒ薼 */
    public static final String SHARED_PREFS_NAME = "BadAppleSettings";
    /** �g�p����摜�̕� */
    public static final int IMG_WIDTH = 512;
    /** �g�p����摜�̍��� */
    public static final int IMG_HEIGHT = 384;
    /** �g�p����摜�̖��� */
    public static final int IMG_COUNT = 6570;

    /* (�� Javadoc)
     * @see android.service.wallpaper.WallpaperService#onCreateEngine()
     */
    @Override
    public Engine onCreateEngine() {
        return new BadAppleLiveWallpaperEngine();
    }

    /**
     * ���C�u�ǎ��̃G���W��
     */
    class BadAppleLiveWallpaperEngine extends Engine implements SharedPreferences.OnSharedPreferenceChangeListener {

        /** �n���h���[ */
        private final Handler mHandler = new Handler();
        /** �`��p�X���b�h */
        private final Runnable mDrawBadApple = new Runnable() {

            @Override
            public void run() {
                drawFrame();
            }

        };
        /** ���L�ݒ� */
        private SharedPreferences mSharedPref;
        /** �摜�t�@�C���A�N�Z�X */
        private Resources mResource;
        /** �摜�ǂݍ��� */
        private Bitmap mBmp;
        /** �`��t���O */
        private boolean mVisible;
        /** ��ʉ��� */
        private int mWidth;
        /** ��ʍ��� */
        private int mHeight;
        /** �z�[����ʃX���C�v���̃I�t�Z�b�g */
        private int mXPixelOffset;
        /** �z�[����ʃX���C�v���̃I�t�Z�b�g */
        private int mYPixelOffset;
        /** ������̕`��摜�̉��� */
        private int mFrameWidth;
        /** ������̕`��摜�̍��� */
        private int mFrameHeight;
        /** �`�撆�摜�̔ԍ� */
        private int mBmpCount;
        /** �J�n����(�`��^�C�~���O�v��) */
        private long mStart;
        /** �`��t���[���擾����(�^�C�~���O�v��) */
        private long mMiddle;
        /** �I������(�`��^�C�~���O�v��)*/
        private long mEnd;
        /** �x������ */
        private long mDelay;
        /** 1�b������̕`�文�� */
        private int mFps;
        /** �A�X�y�N�g�� */
        private String mAspect;
        /** ���� */
        private String mDirection;
        /** ��]�s�� */
        private Matrix mMatrix;


        /**
         * �R���X�g���N�^
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
                // ���l�ȊO���擾�����珉���l�������I�ɐݒ肷��
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

        /* (�� Javadoc)
         * @see android.service.wallpaper.WallpaperService.Engine#onSurfaceChanged(android.view.SurfaceHolder, int, int, int)
         */
        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            super.onSurfaceChanged(holder, format, width, height);
            // ��ʃT�C�Y���擾���ĕ`��
            mWidth = width;
            mHeight = height;
            drawFrame();
        }

        /* (�� Javadoc)
         * @see android.service.wallpaper.WallpaperService.Engine#onSurfaceDestroyed(android.view.SurfaceHolder)
         */
        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            super.onSurfaceDestroyed(holder);
            // ���̕`��\����L�����Z��
            mVisible = false;
            mHandler.removeCallbacks(mDrawBadApple);
        }

        /* (�� Javadoc)
         * @see android.service.wallpaper.WallpaperService.Engine#onVisibilityChanged(boolean)
         */
        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);
            mVisible = visible;
            if (visible) {
                // �`��
                drawFrame();
            } else {
                // �`��L�����Z��
                mHandler.removeCallbacks(mDrawBadApple);
            }
        }

        /* (�� Javadoc)
         * @see android.service.wallpaper.WallpaperService.Engine#onOffsetsChanged(float, float, float, float, int, int)
         */
        @Override
        public void onOffsetsChanged(float xOffset, float yOffset, float xOffsetStep, float yOffsetStep, int xPixelOffset, int yPixelOffset) {
            super.onOffsetsChanged(xOffset, yOffset, xOffsetStep, yOffsetStep, xPixelOffset, yPixelOffset);
            mXPixelOffset = xPixelOffset;
            mYPixelOffset = yPixelOffset;
        }

        /* (�� Javadoc)
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
                // ���l�ȊO���擾�����珉���l�������I�ɐݒ肷��
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
         * BadApple�e�G�̉摜��`�悵�܂��B
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
                    // �`��t���[���ԍ��̎Z�o
                    mMiddle = System.currentTimeMillis();
                    mBmpCount = (int) (((mMiddle - mStart) * 30 / 1000) % IMG_COUNT);
                    String imgNumber = String.format("%1$04d", mBmpCount);
                    // �摜�擾
                    mBmp = BitmapFactory.decodeStream(mResource.getAssets().open("BadAppleImg/BadApple_" + imgNumber +".jpg"));

                    if (mDirection.equals("portrait")) {
                        // �`��
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
                        // ��]
                        mBmp = Bitmap.createBitmap(mBmp, 0, 0, 512, 384, mMatrix, true);
                        // �`��
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
                // �f�B���C�̐ݒ�
                mEnd = System.currentTimeMillis();
                mDelay = (1000 / mFps) - (mEnd - mStart) % (1000 / mFps);
                // �V�X�e�����ׂ̊ϓ_����Œ�10ms�̗]�T����������
                // ����x��H�C�ɂ��Ȃ�
                if (mDelay < 10) {
                    mDelay = 10;
                }
                mHandler.postDelayed(mDrawBadApple, mDelay);
            }
        }

    }

}
