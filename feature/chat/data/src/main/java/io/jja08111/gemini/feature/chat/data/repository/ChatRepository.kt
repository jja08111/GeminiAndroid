package io.jja08111.gemini.feature.chat.data.repository

import io.jja08111.gemini.model.MessageGroup
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
  fun join(roomId: String): Flow<List<MessageGroup>>

  suspend fun sendTextMessage(
    message: String,
    messageGroups: List<MessageGroup>,
    parentModelResponseId: String?,
    onRoomCreated: (Flow<List<MessageGroup>>) -> Unit,
  ): Result<Unit>

  fun exit()
}
