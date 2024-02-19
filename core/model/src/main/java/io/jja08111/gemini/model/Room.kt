package io.jja08111.gemini.model

import java.util.Date

data class Room(
  val id: String,
  val createdAt: Date,
  val recentMessage: Message?,
)
