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
        COALESCE(recent_prompt.max_created_at, room.created_at) AS activated_at
      FROM room
        LEFT JOIN (
          SELECT room_id, MAX(prompt.created_at) AS max_created_at
          FROM prompt
          GROUP BY room_id
        ) AS recent_prompt ON room.id = recent_prompt.room_id
      ORDER BY activated_at DESC
    """,
  )
  fun getRooms(): PagingSource<Int, RoomWithActivatedTime>

  @Insert(onConflict = REPLACE)
  suspend fun insert(roomEntity: RoomEntity)
}
