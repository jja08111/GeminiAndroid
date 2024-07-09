package io.jja08111.gemini.feature.chat.ui.select.response

import io.jja08111.gemini.core.ui.Message

sealed class SelectResponseSideEffect {
  data object PopBackStack : SelectResponseSideEffect()

  data class UserMessage(val message: Message) : SelectResponseSideEffect()
}
