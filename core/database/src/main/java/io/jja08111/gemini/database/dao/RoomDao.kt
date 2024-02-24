package io.jja08111.gemini.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import androidx.room.Transaction
import io.jja08111.gemini.database.entity.RoomEntity
import io.jja08111.gemini.database.entity.RoomWithRecentMessage

@Dao
interface RoomDao {
  @Transaction
  @Query(
    """
      SELECT 
        RoomEntity.id AS id,
        RoomEntity.created_at AS created_at,
        MessageEntity.id AS message_id,
        MessageEntity.role AS message_role,
        MessageEntity.content AS message_content, 
        MessageEntity.type AS message_type, 
        MessageEntity.is_error AS message_is_error, 
        MessageEntity.created_at AS message_created_at
      FROM RoomEntity
      LEFT JOIN (
        SELECT room_id, MAX(created_at) AS maxCreatedAt
        FROM MessageEntity
        GROUP BY room_id
      ) AS recentMessage ON RoomEntity.id = recentMessage.room_id
      LEFT JOIN MessageEntity ON recentMessage.room_id = MessageEntity.room_id 
        AND recentMessage.maxCreatedAt = MessageEntity.created_at
      ORDER BY COALESCE(recentMessage.maxCreatedAt, RoomEntity.created_at) DESC
    """,
  )
  fun getRooms(): PagingSource<Int, RoomWithRecentMessage>

  @Insert(onConflict = REPLACE)
  suspend fun insert(roomEntity: RoomEntity)
}
