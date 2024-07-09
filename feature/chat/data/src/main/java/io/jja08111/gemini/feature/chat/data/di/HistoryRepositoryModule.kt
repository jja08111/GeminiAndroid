package io.jja08111.gemini.feature.chat.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.jja08111.gemini.feature.chat.data.repository.HistoryRepository
import io.jja08111.gemini.feature.chat.data.repository.LocalHistoryRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class HistoryRepositoryModule {
  @Binds
  @Singleton
  abstract fun bindsHistoryRepository(historyRepository: LocalHistoryRepository): HistoryRepository
}
