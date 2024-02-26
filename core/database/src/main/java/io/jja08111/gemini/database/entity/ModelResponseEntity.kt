package io.jja08111.gemini.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
  tableName = "model_response",
  foreignKeys = [
    ForeignKey(
      entity = RoomEntity::class,
      parentColumns = ["id"],
      childColumns = ["room_id"],
      onDelete = ForeignKey.CASCADE,
    ),
    ForeignKey(
      entity = PromptEntity::class,
      parentColumns = ["id"],
      childColumns = ["parent_prompt_id"],
      onDelete = ForeignKey.CASCADE,
    ),
  ],
)
data class ModelResponseEntity(
  @PrimaryKey val id: String,
  @ColumnInfo("room_id") val roomId: String,
  @ColumnInfo("parent_prompt_id") val parentPromptId: String,
  @ColumnInfo("text") val text: String,
  @ColumnInfo("state") val state: ModelResponseStateEntity,
  @ColumnInfo("selected") val selected: Boolean,
  @ColumnInfo("created_at") val createdAt: Long,
)
