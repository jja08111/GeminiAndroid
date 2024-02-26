package io.jja08111.gemini.database.entity.partial

import androidx.room.ColumnInfo

data class ModelResponseContentPartial(
  @ColumnInfo("id") val id: String,
  @ColumnInfo("text") val text: String,
)
