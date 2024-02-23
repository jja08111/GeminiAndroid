package io.jja08111.gemini.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import io.jja08111.gemini.database.entity.MessageEntity

@Dao
interface MessageDao {
  @Query("SELECT * FROM MessageEntity WHERE room_id = :roomId ORDER BY created_at DESC")
  fun getMessagePagingSource(roomId: String): PagingSource<Int, MessageEntity>

  @Query("SELECT * FROM MessageEntity WHERE room_id = :roomId")
  suspend fun getAllMessages(roomId: String): List<MessageEntity>

  @Insert
  suspend fun insert(messageEntity: MessageEntity)
}
