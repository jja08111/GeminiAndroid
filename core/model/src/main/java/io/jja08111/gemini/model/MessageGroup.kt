package io.jja08111.gemini.model

data class MessageGroup(
  val prompt: Prompt,
  val selectedResponse: ModelResponse,
  val responseCount: Int,
)
