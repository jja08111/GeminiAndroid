package io.jja08111.gemini.feature.rooms.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.jja08111.gemini.feature.rooms.data.repository.LocalRoomRepository
import io.jja08111.gemini.feature.rooms.data.repository.RoomRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RoomRepositoryModule {
  @Binds
  @Singleton
  abstract fun bindsRoomRepository(localRoomRepository: LocalRoomRepository): RoomRepository
}
