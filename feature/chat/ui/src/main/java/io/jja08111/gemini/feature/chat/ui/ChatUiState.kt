package io.jja08111.gemini.feature.chat.ui

import io.jja08111.gemini.model.Message
import kotlinx.coroutines.flow.Flow

data class ChatUiState(
  val messageStream: Flow<List<Message>>,
  val inputMessage: String = "",
) {
  val isGenerating: Boolean
    get() = false

  val canSendMessage: Boolean
    get() = !isGenerating && inputMessage.isNotEmpty()
}
