package io.jja08111.gemini.feature.chat.ui

import io.jja08111.gemini.core.ui.StringValue

sealed class ChatSideEffect {
  data class UserMessage(val message: StringValue) : ChatSideEffect()
}
