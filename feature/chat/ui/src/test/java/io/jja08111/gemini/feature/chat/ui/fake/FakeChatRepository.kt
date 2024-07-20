package io.jja08111.gemini.feature.chat.ui.fake

import io.jja08111.gemini.feature.chat.data.model.AttachedImage
import io.jja08111.gemini.feature.chat.data.repository.ChatRepository
import io.jja08111.gemini.model.MessageGroup
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeChatRepository : ChatRepository {
  override fun join(roomId: String): Flow<List<MessageGroup>> {
    return flowOf(emptyList())
  }

  override suspend fun sendMessage(
    message: String,
    images: List<AttachedImage>,
    onRoomCreated: (Flow<List<MessageGroup>>) -> Unit,
  ): Result<Unit> {
    return Result.success(Unit)
  }

  override suspend fun regenerateOnError(): Result<Unit> {
    return Result.success(Unit)
  }

  override suspend fun regenerateResponse(responseId: String): Result<Unit> {
    return Result.success(Unit)
  }

  override fun exit() {}
}
