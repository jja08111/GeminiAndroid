package io.jja08111.gemini.feature.chat.data.extension

import com.google.ai.client.generativeai.type.Content
import com.google.ai.client.generativeai.type.content
import io.jja08111.gemini.database.entity.ModelResponseEntity
import io.jja08111.gemini.database.entity.PromptEntity
import io.jja08111.gemini.database.extension.toDomain
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

internal fun convertToMessageGroups(
  promptAndMessage: Map<PromptEntity, List<ModelResponseEntity>>,
): List<MessageGroup> {
  val result = mutableListOf<MessageGroup>()
  promptAndMessage.forEach { (prompt, responses) ->
    val selectedResponse = responses.find { it.selected } ?: error("There is no selected response.")
    val lastResponse = result.lastOrNull()?.selectedResponse
    if (result.isEmpty() || prompt.parentModelResponseId == lastResponse?.id) {
      val messageGroup = MessageGroup(
        prompt = prompt.toDomain(),
        selectedResponse = selectedResponse.toDomain(),
        responseCount = responses.size,
      )
      result.add(messageGroup)
    }
  }
  return result
}
