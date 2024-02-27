package io.jja08111.gemini.feature.chat.data.model

import java.util.UUID

internal data class ResponseTextBuilder(
  val id: String = UUID.randomUUID().toString(),
  val textBuilder: StringBuilder = StringBuilder(),
)
