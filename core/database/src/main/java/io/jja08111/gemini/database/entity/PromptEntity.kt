package io.jja08111.gemini.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(
  tableName = "prompt",
  foreignKeys = [
    ForeignKey(
      entity = RoomEntity::class,
      parentColumns = ["id"],
      childColumns = ["room_id"],
      onDelete = ForeignKey.CASCADE,
    ),
    ForeignKey(
      entity = ModelResponseEntity::class,
      parentColumns = ["id"],
      childColumns = ["parent_model_response_id"],
      onDelete = ForeignKey.CASCADE,
    ),
  ],
)
data class PromptEntity(
  @PrimaryKey val id: String,
  @ColumnInfo("room_id") val roomId: String,
  @ColumnInfo("parent_model_response_id") val parentModelResponseId: String?,
  @ColumnInfo("text") val text: String,
  @ColumnInfo("created_at") val createdAt: LocalDateTime,
)
