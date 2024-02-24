package io.jja08111.gemini.database.extension

import io.jja08111.gemini.database.entity.RoomWithRecentMessage
import io.jja08111.gemini.model.Room
import java.util.Date

fun RoomWithRecentMessage.toDomain(): Room {
  return Room(
    id = roomEntity.id,
    createdAt = Date(roomEntity.createdAt),
    recentMessage = messageEntity?.toDomain(),
  )
}
