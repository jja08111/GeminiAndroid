package io.jja08111.gemini.model

import java.util.Date
import java.util.UUID

data class ModelResponse(
  val id: String = UUID.randomUUID().toString(),
  val text: String,
  val roomId: String,
  val selected: Boolean,
  val state: ModelResponseState = ModelResponseState.Generating,
  val createdAt: Date,
)
