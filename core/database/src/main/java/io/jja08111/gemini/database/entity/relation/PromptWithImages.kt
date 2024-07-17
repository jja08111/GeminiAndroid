package io.jja08111.gemini.database.entity.relation

import androidx.room.Embedded
import androidx.room.Relation
import io.jja08111.gemini.database.entity.PromptEntity
import io.jja08111.gemini.database.entity.PromptImageEntity

data class PromptWithImages(
  @Embedded val prompt: PromptEntity,
  @Relation(
    parentColumn = "id",
    entityColumn = "prompt_id",
  ) val images: List<PromptImageEntity>,
)
