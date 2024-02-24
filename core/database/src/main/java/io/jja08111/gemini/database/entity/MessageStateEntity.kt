package io.jja08111.gemini.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import io.jja08111.gemini.model.MessageState

@Entity
data class MessageStateEntity(
  @ColumnInfo("id") val id: String,
  @ColumnInfo("state") val state: MessageState,
)
