/**
 * Copyright (C) 2023 lailai.
 */
package net.lailai.android.badapple.livepaper.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.lailai.android.badapple.livepaper.data.PreferenceRepository
import javax.inject.Singleton

/**
 * RepositoryのHilt設定
 * @author lailai
 */
@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    @Singleton
    fun providePreferenceRepository(@ApplicationContext context: Context): PreferenceRepository {
        return PreferenceRepository(context)
    }
}
