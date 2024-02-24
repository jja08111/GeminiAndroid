package io.jja08111.gemini.database.entity

import androidx.room.Embedded

data class RoomWithRecentMessage(
  @Embedded val roomEntity: RoomEntity,
  @Embedded(prefix = "message_") val messageEntity: MessageEntity?,
)
