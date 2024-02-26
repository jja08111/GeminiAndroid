package io.jja08111.gemini.database.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded

data class RoomWithActivatedTime(
  @Embedded val room: RoomEntity,
  @ColumnInfo("activated_at") val activatedAt: Long,
)
