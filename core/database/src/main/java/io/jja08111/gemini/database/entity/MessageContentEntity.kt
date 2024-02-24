package io.jja08111.gemini.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity
data class MessageContentEntity(
  @ColumnInfo("id") val id: String,
  @ColumnInfo("content") val content: String,
)
