package io.jja08111.gemini.database.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import io.jja08111.gemini.database.converter.LocalDateTimeConverter
import io.jja08111.gemini.database.dao.MessageDao
import io.jja08111.gemini.database.dao.RoomDao
import io.jja08111.gemini.database.entity.ModelResponseEntity
import io.jja08111.gemini.database.entity.PromptEntity
import io.jja08111.gemini.database.entity.RoomEntity

@Database(
  entities = [RoomEntity::class, PromptEntity::class, ModelResponseEntity::class],
  version = 1,
)
@TypeConverters(value = [LocalDateTimeConverter::class])
internal abstract class GeminiDatabase : RoomDatabase() {
  abstract fun messageDao(): MessageDao

  abstract fun roomDao(): RoomDao
}
