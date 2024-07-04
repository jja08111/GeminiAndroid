package io.jja08111.gemini.model

import java.util.Date

data class Prompt(
  val id: String = createId(),
  val roomId: String,
  val createdAt: Date = Date(),
  val text: String,
)
