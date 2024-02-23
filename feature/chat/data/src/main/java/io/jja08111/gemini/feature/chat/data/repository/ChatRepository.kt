package io.jja08111.gemini.feature.chat.data.repository

import androidx.paging.PagingData
import com.google.ai.client.generativeai.type.GenerateContentResponse
import io.jja08111.gemini.model.Message
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
  fun getMessageStream(roomId: String): Flow<PagingData<Message>>

  suspend fun join(roomId: String)

  suspend fun sendTextMessage(message: String, id: String): Flow<GenerateContentResponse>

  fun exit()
}
