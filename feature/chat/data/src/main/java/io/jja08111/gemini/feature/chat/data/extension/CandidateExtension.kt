package io.jja08111.gemini.feature.chat.data.extension

import com.google.ai.client.generativeai.type.Candidate
import com.google.ai.client.generativeai.type.asTextOrNull
import io.jja08111.gemini.database.entity.partial.ModelResponseContentPartial
import io.jja08111.gemini.feature.chat.data.model.ResponseTextBuilder

internal fun List<Candidate>.toResponseContentPartials(
  responseTextBuilders: List<ResponseTextBuilder>,
): List<ModelResponseContentPartial> {
  return mapIndexedNotNull map@{ index, candidate ->
    val responseHolder = responseTextBuilders[index]
    val parts = candidate.content.parts
    val text = parts.firstOrNull()?.asTextOrNull() ?: return@map null
    val responseTextBuilder = responseHolder.textBuilder

    responseTextBuilder.append(text)

    return@map ModelResponseContentPartial(
      id = responseHolder.id,
      text = responseTextBuilder.toString(),
    )
  }
}
