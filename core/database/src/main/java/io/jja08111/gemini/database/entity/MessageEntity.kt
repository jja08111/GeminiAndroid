package io.jja08111.gemini.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.PrimaryKey
import io.jja08111.gemini.model.ContentType
import io.jja08111.gemini.model.MessageState
import io.jja08111.gemini.model.Role

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
  @ColumnInfo("role") val role: Role,
  @ColumnInfo("type") val type: ContentType,
  @ColumnInfo("created_at") val createdAt: Long,
  @ColumnInfo("state") val state: MessageState,
)
