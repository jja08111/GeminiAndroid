package io.jja08111.gemini.feature.chat.data.repository

import io.jja08111.gemini.feature.chat.data.model.AttachedImage
import io.jja08111.gemini.model.MessageGroup
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
  fun join(roomId: String): Flow<List<MessageGroup>>

  suspend fun sendMessage(
    message: String,
    images: List<AttachedImage>,
    onRoomCreated: (Flow<List<MessageGroup>>) -> Unit,
  ): Result<Unit>

  suspend fun regenerateOnError(): Result<Unit>

  suspend fun regenerateResponse(responseId: String): Result<Unit>

  fun exit()
}
