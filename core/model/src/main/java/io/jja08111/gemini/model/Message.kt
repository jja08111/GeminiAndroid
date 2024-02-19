package io.jja08111.gemini.model

import java.util.Date

data class Message(
  val id: String,
  val roomId: String,
  val createdAt: Date,
  val content: Content,
)
