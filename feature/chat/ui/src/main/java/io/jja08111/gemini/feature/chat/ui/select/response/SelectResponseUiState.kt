package io.jja08111.gemini.feature.chat.ui.select.response

import io.jja08111.gemini.model.ModelResponse
import io.jja08111.gemini.model.Prompt
import kotlinx.coroutines.flow.Flow

data class SelectResponseUiState(
  val promptStream: Flow<Prompt>,
  val responsesStream: Flow<List<ModelResponse>>,
)
