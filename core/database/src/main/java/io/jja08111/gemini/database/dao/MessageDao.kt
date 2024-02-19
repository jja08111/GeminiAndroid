package io.jja08111.gemini.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import io.jja08111.gemini.database.entity.MessageEntity

@Dao
interface MessageDao {
  @Query("SELECT * FROM MessageEntity WHERE room_id = :roomId")
  suspend fun getMessages(roomId: String): List<MessageEntity>

  @Insert
  suspend fun insert(messageEntity: MessageEntity)
}
