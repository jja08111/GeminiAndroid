package io.jja08111.gemini.feature.chat.ui

import io.jja08111.gemini.model.MessageGroup
import kotlinx.coroutines.flow.Flow

data class ChatUiState(
  val messageGroupStream: Flow<List<MessageGroup>>,
  val inputMessage: String = "",
)
