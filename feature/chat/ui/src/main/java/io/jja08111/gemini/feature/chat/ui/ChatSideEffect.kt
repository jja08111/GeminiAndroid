package io.jja08111.gemini.feature.chat.ui

sealed class ChatSideEffect {
  data object MessageResponded : ChatSideEffect()
}
