package io.jja08111.gemini.feature.chat.data.model

import io.jja08111.gemini.model.createId

internal data class ResponseTextBuilder(
  val id: String = createId(),
  val textBuilder: StringBuilder = StringBuilder(),
)
