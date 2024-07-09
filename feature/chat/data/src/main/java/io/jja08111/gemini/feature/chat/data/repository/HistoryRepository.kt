package io.jja08111.gemini.feature.chat.data.repository

import io.jja08111.gemini.model.ModelResponse
import io.jja08111.gemini.model.Prompt
import kotlinx.coroutines.flow.Flow

interface HistoryRepository {
  fun getPromptFlow(promptId: String): Flow<Prompt>

  fun getResponsesFlow(parentPromptId: String): Flow<List<ModelResponse>>

  suspend fun changeSelectedResponse(promptId: String, responseId: String): Result<Unit>
}
