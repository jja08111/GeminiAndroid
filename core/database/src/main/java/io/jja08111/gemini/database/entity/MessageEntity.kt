package io.jja08111.gemini.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.PrimaryKey

@Entity(
  foreignKeys = [
    ForeignKey(
      entity = RoomEntity::class,
      parentColumns = ["id"],
      childColumns = ["room_id"],
      onDelete = CASCADE,
    ),
  ],
)
data class MessageEntity(
  @PrimaryKey val id: String,
  @ColumnInfo("room_id") val roomId: String,
  @ColumnInfo("content") val content: String,
  @ColumnInfo("role") val role: String,
  @ColumnInfo("type") val type: String,
  @ColumnInfo("created_at") val createdAt: Long,
  @ColumnInfo("is_error", defaultValue = "0") val isError: Boolean,
)
