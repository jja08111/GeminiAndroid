package io.jja08111.gemini.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity
data class MessageErrorEntity(
  @ColumnInfo("id") val id: String,
  @ColumnInfo("room_id") val roomId: String,
  @ColumnInfo("is_error") val isError: Boolean = true,
)
