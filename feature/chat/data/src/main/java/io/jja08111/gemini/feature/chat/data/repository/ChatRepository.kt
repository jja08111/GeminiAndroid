package io.jja08111.gemini.feature.chat.data.repository

import io.jja08111.gemini.model.Message
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
  fun join(roomId: String): Flow<List<Message>>

  suspend fun sendTextMessage(message: String, id: String): Result<Unit>

  fun exit()
}
