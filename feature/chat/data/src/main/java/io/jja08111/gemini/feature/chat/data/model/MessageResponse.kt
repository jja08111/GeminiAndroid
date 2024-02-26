package io.jja08111.gemini.feature.chat.data.model

import com.google.ai.client.generativeai.type.GenerateContentResponse

sealed interface MessageResponse

data class MessageResponseData(
  val content: GenerateContentResponse,
) : MessageResponse

data object MessageResponseFinished : MessageResponse
