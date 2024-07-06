package io.jja08111.gemini.model

import java.time.LocalDateTime

data class Room(
  val id: String,
  val title: String?,
  val createdAt: LocalDateTime,
  val activatedAt: LocalDateTime,
)
