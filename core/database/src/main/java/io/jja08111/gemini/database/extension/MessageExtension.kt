package io.jja08111.gemini.database.extension

import io.jja08111.gemini.database.entity.ModelResponseEntity
import io.jja08111.gemini.database.entity.ModelResponseStateEntity
import io.jja08111.gemini.database.entity.PromptEntity
import io.jja08111.gemini.model.ModelResponse
import io.jja08111.gemini.model.ModelResponseState
import io.jja08111.gemini.model.Prompt

fun PromptEntity.toDomain() =
  Prompt(
    id = id,
    roomId = roomId,
    text = text,
    createdAt = createdAt,
  )

fun ModelResponseEntity.toDomain() =
  ModelResponse(
    id = id,
    roomId = roomId,
    selected = selected,
    text = text,
    state = state.toDomain(),
    createdAt = createdAt,
  )

fun ModelResponseStateEntity.toDomain() =
  when (this) {
    ModelResponseStateEntity.Generating -> ModelResponseState.Generating
    ModelResponseStateEntity.Error -> ModelResponseState.Error
    ModelResponseStateEntity.Generated -> ModelResponseState.Generated
  }
