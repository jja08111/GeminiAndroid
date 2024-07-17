package io.jja08111.gemini.database.extension

import io.jja08111.gemini.database.entity.ModelResponseEntity
import io.jja08111.gemini.database.entity.ModelResponseStateEntity
import io.jja08111.gemini.database.entity.PromptEntity
import io.jja08111.gemini.database.entity.PromptImageEntity
import io.jja08111.gemini.database.entity.relation.PromptWithImages
import io.jja08111.gemini.model.ModelResponse
import io.jja08111.gemini.model.ModelResponseState
import io.jja08111.gemini.model.Prompt
import io.jja08111.gemini.model.PromptImage

fun PromptImageEntity.toDomain() =
  PromptImage(
    width = width,
    height = height,
    path = path,
  )

fun PromptEntity.toDomain(images: List<PromptImageEntity> = emptyList()) =
  Prompt(
    id = id,
    roomId = roomId,
    text = text,
    createdAt = createdAt,
    images = images.map { it.toDomain() },
  )

fun PromptWithImages.toDomain() =
  Prompt(
    id = prompt.id,
    roomId = prompt.roomId,
    text = prompt.text,
    createdAt = prompt.createdAt,
    images = images.map { it.toDomain() },
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
