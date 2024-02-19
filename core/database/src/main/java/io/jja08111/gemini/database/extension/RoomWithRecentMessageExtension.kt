package io.jja08111.gemini.database.extension

import io.jja08111.gemini.database.entity.RoomWithRecentMessage
import io.jja08111.gemini.model.Content
import io.jja08111.gemini.model.Message
import io.jja08111.gemini.model.Room
import java.util.Date

fun RoomWithRecentMessage.toDomain(): Room {
  // TODO: 아래보다 더 나은 방법 찾아보기
  val recentMessage = if (messageId != null && messageType != null && messageCreatedAt != null) {
    Message(
      id = messageId,
      roomId = roomEntity.id,
      content = Content.of(messageType),
      createdAt = Date(messageCreatedAt),
    )
  } else {
    null
  }
  return Room(
    id = roomEntity.id,
    createdAt = Date(roomEntity.createdAt),
    recentMessage = recentMessage,
  )
}
