package io.jja08111.gemini.feature.chat.ui

import io.jja08111.gemini.model.MessageGroup
import io.jja08111.gemini.model.ModelResponseState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest

data class ChatUiState(
  val messageGroupStream: Flow<List<MessageGroup>>,
  val inputMessage: String = "",
) {
  val isGenerating: Flow<Boolean> = messageGroupStream.mapLatest { messageGroup ->
    messageGroup.lastOrNull()?.selectedResponse?.state == ModelResponseState.Generating
  }
}
