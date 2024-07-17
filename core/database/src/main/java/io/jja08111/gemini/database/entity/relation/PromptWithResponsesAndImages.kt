package io.jja08111.gemini.database.entity.relation

import androidx.room.Embedded
import androidx.room.Relation
import io.jja08111.gemini.database.entity.ModelResponseEntity
import io.jja08111.gemini.database.entity.PromptEntity
import io.jja08111.gemini.database.entity.PromptImageEntity

data class PromptWithResponsesAndImages(
  @Embedded val prompt: PromptEntity,
  @Relation(
    parentColumn = "id",
    entityColumn = "prompt_id",
  ) val images: List<PromptImageEntity>,
  @Relation(
    parentColumn = "id",
    entityColumn = "parent_prompt_id",
  ) val responses: List<ModelResponseEntity>,
)
