package io.jja08111.gemini.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import androidx.room.Transaction
import io.jja08111.gemini.database.entity.RoomEntity
import io.jja08111.gemini.database.entity.RoomWithActivatedTime

@Dao
interface RoomDao {
  @Transaction
  @Query(
    """
      SELECT 
        room.*,
        COALESCE(prompt.created_at, room.created_at) AS activated_at
      FROM room
        LEFT JOIN (
          SELECT room_id, MAX(created_at) AS maxCreatedAt
          FROM prompt
          GROUP BY room_id
        ) AS recent_prompt ON room.id = recent_prompt.room_id
        LEFT JOIN prompt ON recent_prompt.room_id = prompt.room_id 
          AND recent_prompt.maxCreatedAt = prompt.created_at
      ORDER BY COALESCE(recent_prompt.maxCreatedAt, room.created_at) DESC
    """,
  )
  fun getRooms(): PagingSource<Int, RoomWithActivatedTime>

  @Insert(onConflict = REPLACE)
  suspend fun insert(roomEntity: RoomEntity)
}
