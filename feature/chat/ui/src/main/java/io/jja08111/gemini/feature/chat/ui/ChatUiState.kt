package io.jja08111.gemini.feature.chat.ui

import androidx.paging.PagingData
import io.jja08111.gemini.model.Message
import kotlinx.coroutines.flow.Flow

data class ChatUiState(
  val messageStream: Flow<PagingData<Message>>,
  val respondingMessage: String? = null,
  val inputMessage: String = "",
  val generatingMessageId: String? = null,
) {
  val isGenerating: Boolean
    get() = generatingMessageId != null

  val canSendMessage: Boolean
    get() = !isGenerating && inputMessage.isNotEmpty()
}
