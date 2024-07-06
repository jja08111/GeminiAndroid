package io.jja08111.gemini.model

import java.time.LocalDateTime

data class ModelResponse(
  val id: String = createId(),
  val text: String,
  val roomId: String,
  val selected: Boolean,
  val state: ModelResponseState = ModelResponseState.Generating,
  val createdAt: LocalDateTime,
)
