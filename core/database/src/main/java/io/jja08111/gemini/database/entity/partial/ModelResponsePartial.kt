package io.jja08111.gemini.database.entity.partial

import androidx.room.ColumnInfo
import io.jja08111.gemini.database.entity.ModelResponseStateEntity

data class ModelResponsePartial(
  @ColumnInfo("id") val id: String,
  @ColumnInfo("text") val text: String,
  @ColumnInfo("state") val state: ModelResponseStateEntity,
)
