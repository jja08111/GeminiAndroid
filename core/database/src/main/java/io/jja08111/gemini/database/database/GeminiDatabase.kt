package io.jja08111.gemini.database.database

import androidx.room.Database
import androidx.room.RoomDatabase
import io.jja08111.gemini.database.dao.MessageDao
import io.jja08111.gemini.database.dao.RoomDao
import io.jja08111.gemini.database.entity.MessageEntity
import io.jja08111.gemini.database.entity.RoomEntity

@Database(entities = [RoomEntity::class, MessageEntity::class], version = 1)
internal abstract class GeminiDatabase : RoomDatabase() {
  abstract fun messageDao(): MessageDao

  abstract fun roomDao(): RoomDao
}
