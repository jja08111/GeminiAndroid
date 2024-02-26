package io.jja08111.gemini.feature.chat.data.extension

import com.google.ai.client.generativeai.type.Content
import com.google.ai.client.generativeai.type.content
import io.jja08111.gemini.feature.chat.data.model.ROLE_MODEL
import io.jja08111.gemini.feature.chat.data.model.ROLE_USER
import io.jja08111.gemini.model.MessageGroup

fun MessageGroup.toContents(): List<Content> {
  val promptContent = content {
    role = ROLE_USER
    text(prompt.text)
  }
  val responseContent = content {
    role = ROLE_MODEL
    text(selectedResponse.text)
  }
  return listOf(promptContent, responseContent)
}
