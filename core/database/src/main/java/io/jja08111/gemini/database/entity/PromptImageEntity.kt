package io.jja08111.gemini.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
  tableName = "prompt_image",
  foreignKeys = [
    ForeignKey(
      entity = PromptEntity::class,
      parentColumns = ["id"],
      childColumns = ["prompt_id"],
      onDelete = ForeignKey.CASCADE,
    ),
  ],
)
data class PromptImageEntity(
  @PrimaryKey val id: String,
  @ColumnInfo("prompt_id") val promptId: String,
  @ColumnInfo("width") val width: Int,
  @ColumnInfo("height") val height: Int,
  @ColumnInfo("path") val path: String,
)
