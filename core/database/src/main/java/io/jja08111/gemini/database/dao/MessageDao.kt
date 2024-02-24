package io.jja08111.gemini.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import io.jja08111.gemini.database.entity.MessageContentEntity
import io.jja08111.gemini.database.entity.MessageEntity
import io.jja08111.gemini.database.entity.MessageStateEntity
import io.jja08111.gemini.model.MessageState
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {
  @Query("SELECT * FROM MessageEntity WHERE room_id = :roomId ORDER BY created_at ASC")
  fun getAllMessages(roomId: String): Flow<List<MessageEntity>>

  @Insert
  suspend fun insert(vararg messageEntity: MessageEntity)

  @Update(entity = MessageEntity::class)
  suspend fun update(message: MessageContentEntity)

  @Update(entity = MessageEntity::class)
  suspend fun update(message: MessageStateEntity)

  @Query("UPDATE MessageEntity SET state = :newState WHERE state = :oldState")
  fun updateAllMessagesState(oldState: MessageState, newState: MessageState)
}
