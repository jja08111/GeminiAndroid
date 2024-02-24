package io.jja08111.gemini.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity
data class MessageErrorEntity(
  @ColumnInfo("id") val id: String,
  @ColumnInfo("is_error") val isError: Boolean,
)
