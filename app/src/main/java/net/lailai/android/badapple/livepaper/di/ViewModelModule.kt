/**
 * Copyright (C) 2023 lailai.
 */
package net.lailai.android.badapple.livepaper.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import net.lailai.android.badapple.livepaper.data.PreferenceRepository
import net.lailai.android.badapple.livepaper.ui.wallpaper.BadAppleLiveWallpaperViewModel
import javax.inject.Singleton

/**
 * ViewModelのHilt設定
 * @author lailai
 */
@Module
@InstallIn(SingletonComponent::class)
object ViewModelModule {
    @Provides
    @Singleton
    fun provideBadAppleLiveWallpaperViewModel(
        pfRepository: PreferenceRepository
    ): BadAppleLiveWallpaperViewModel {
        return BadAppleLiveWallpaperViewModel(pfRepository)
    }
}
