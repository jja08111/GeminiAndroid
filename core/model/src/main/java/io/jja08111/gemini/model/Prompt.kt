package io.jja08111.gemini.model

import java.time.LocalDateTime

data class Prompt(
  val id: String = createId(),
  val roomId: String,
  val createdAt: LocalDateTime = LocalDateTime.now(),
  val text: String,
  val images: List<PromptImage>,
)
