package io.jja08111.gemini.database.di

import android.app.Application
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.jja08111.gemini.database.dao.MessageDao
import io.jja08111.gemini.database.dao.RoomDao
import io.jja08111.gemini.database.database.GeminiDatabase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal class GeminiDatabaseModule {
  @Provides
  @Singleton
  fun provideGeminiDatabase(application: Application): GeminiDatabase {
    return Room
      .databaseBuilder(application, GeminiDatabase::class.java, "Gemini.db")
      .fallbackToDestructiveMigration()
      .build()
  }

  @Provides
  @Singleton
  fun provideMessageDao(geminiDatabase: GeminiDatabase): MessageDao {
    return geminiDatabase.messageDao()
  }

  @Provides
  @Singleton
  fun provideRoomDao(geminiDatabase: GeminiDatabase): RoomDao {
    return geminiDatabase.roomDao()
  }
}
