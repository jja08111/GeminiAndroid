package io.jja08111.gemini.feature.chat.ui

import io.jja08111.gemini.model.MessageGroup
import kotlinx.coroutines.flow.Flow

data class ChatUiState(
  val messageGroupStream: Flow<List<MessageGroup>>,
  val generatingMessage: String? = null,
  val inputMessage: String = "",
) {
  val isGenerating: Boolean
    get() = generatingMessage != null

  val canSendMessage: Boolean
    get() = !isGenerating && inputMessage.isNotEmpty()
}
