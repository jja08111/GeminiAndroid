package io.jja08111.gemini.model

import java.util.Date
import java.util.UUID

data class Message(
  val id: String = UUID.randomUUID().toString(),
  val roomId: String,
  val role: Role,
  val createdAt: Date = Date(),
  val content: Content,
  val state: MessageState = MessageState.Generating,
) {
  val isError: Boolean
    get() = state == MessageState.Error

  val isFromUser: Boolean
    get() = role == Role.User
}
