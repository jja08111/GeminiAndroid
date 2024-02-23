package io.jja08111.gemini.feature.chat.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.jja08111.gemini.feature.chat.data.repository.ChatRepository
import io.jja08111.gemini.feature.chat.data.repository.GenerativeChatRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ChatRepositoryModule {
  @Binds
  @Singleton
  abstract fun bindsChatRepository(chatRepository: GenerativeChatRepository): ChatRepository
}
