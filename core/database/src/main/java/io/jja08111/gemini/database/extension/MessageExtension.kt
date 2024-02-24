package io.jja08111.gemini.database.extension

import io.jja08111.gemini.database.entity.MessageEntity
import io.jja08111.gemini.model.Content
import io.jja08111.gemini.model.Message
import io.jja08111.gemini.model.Role
import io.jja08111.gemini.model.TextContent
import java.util.Date

fun Message.toEntity(): MessageEntity {
  return MessageEntity(
    id = id,
    roomId = roomId,
    content = when (val content = content) {
      is TextContent -> content.text
    },
    createdAt = createdAt.time,
    role = role.text,
    type = content.type.rawText,
    state = state,
  )
}

fun MessageEntity.toDomain(): Message {
  return Message(
    id = id,
    roomId = roomId,
    content = Content.of(type = type, content = content),
    createdAt = Date(createdAt),
    role = Role.of(role),
    state = state,
  )
}
