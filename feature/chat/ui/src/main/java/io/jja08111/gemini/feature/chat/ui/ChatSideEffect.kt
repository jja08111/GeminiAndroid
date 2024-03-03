package io.jja08111.gemini.feature.chat.ui

import io.jja08111.gemini.core.ui.Message

sealed class ChatSideEffect {
  data class UserMessage(val message: Message) : ChatSideEffect()
}
