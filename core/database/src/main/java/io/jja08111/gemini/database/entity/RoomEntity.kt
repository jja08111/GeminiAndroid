package io.jja08111.gemini.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
  tableName = "room",
)
data class RoomEntity(
  @PrimaryKey val id: String,
  @ColumnInfo("title") val title: String?,
  @ColumnInfo("created_at") val createdAt: Long,
)
