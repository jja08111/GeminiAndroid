package io.jja08111.gemini.model

import java.util.Date
import java.util.UUID

data class Prompt(
  val id: String = UUID.randomUUID().toString(),
  val roomId: String,
  val createdAt: Date = Date(),
  val text: String,
)
