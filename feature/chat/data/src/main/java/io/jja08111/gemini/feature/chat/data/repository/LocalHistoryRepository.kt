package io.jja08111.gemini.feature.chat.data.repository

import io.jja08111.gemini.feature.chat.data.source.ChatLocalDataSource
import io.jja08111.gemini.model.ModelResponse
import io.jja08111.gemini.model.Prompt
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocalHistoryRepository @Inject constructor(
  private val chatLocalDataSource: ChatLocalDataSource,
) : HistoryRepository {
  override fun getPromptFlow(promptId: String): Flow<Prompt> {
    return chatLocalDataSource.getPromptBy(promptId = promptId)
  }

  override fun getResponsesFlow(parentPromptId: String): Flow<List<ModelResponse>> {
    return chatLocalDataSource.getModelResponsesBy(parentPromptId = parentPromptId)
  }

  override suspend fun changeSelectedResponse(promptId: String, responseId: String): Result<Unit> {
    return runCatching {
      chatLocalDataSource.changeSelectedResponse(promptId = promptId, responseId = responseId)
    }
  }
}
