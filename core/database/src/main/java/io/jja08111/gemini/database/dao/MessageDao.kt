package io.jja08111.gemini.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import io.jja08111.gemini.database.entity.MessageEntity
import io.jja08111.gemini.database.entity.MessageErrorEntity

@Dao
interface MessageDao {
  @Query("SELECT * FROM MessageEntity WHERE room_id = :roomId ORDER BY created_at DESC")
  fun getMessagePagingSource(roomId: String): PagingSource<Int, MessageEntity>

  @Query("SELECT * FROM MessageEntity WHERE room_id = :roomId ORDER BY created_at ASC")
  suspend fun getAllMessages(roomId: String): List<MessageEntity>

  @Insert
  suspend fun insert(messageEntity: MessageEntity)

  @Update(entity = MessageEntity::class)
  suspend fun update(message: MessageErrorEntity)
}
