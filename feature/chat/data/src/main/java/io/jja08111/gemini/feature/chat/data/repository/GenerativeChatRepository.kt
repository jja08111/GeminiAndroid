package io.jja08111.gemini.feature.chat.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.GenerateContentResponse
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import io.jja08111.gemini.database.dao.MessageDao
import io.jja08111.gemini.database.extension.toDomain
import io.jja08111.gemini.database.extension.toEntity
import io.jja08111.gemini.feature.chat.data.BuildConfig
import io.jja08111.gemini.model.Message
import io.jja08111.gemini.model.Role
import io.jja08111.gemini.model.TextContent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class GenerativeChatRepository @Inject constructor(
  private val messageDao: MessageDao,
) : ChatRepository {
  // TODO: Inject from the hilt module
  private val coroutineScope = CoroutineScope(Dispatchers.Default)
  private val currentRoomId = MutableStateFlow<String?>(null)
  private val generativeModel = MutableStateFlow<GenerativeModel?>(null)
  private val generativeChat = generativeModel.mapLatest {
    it?.startChat()
  }.stateIn(
    scope = coroutineScope,
    started = SharingStarted.Eagerly,
    initialValue = null,
  )

  override suspend fun join(roomId: String) {
    currentRoomId.update { roomId }
    generativeModel.update {
      GenerativeModel(
        modelName = "gemini-pro",
        apiKey = BuildConfig.GEMINI_API_KEY,
        generationConfig = generationConfig {
          this.candidateCount = 1
        },
      )
    }
    val content = messageDao.getAllMessages(roomId).map {
      val message = it.toDomain()
      return@map when (message.content) {
        is TextContent -> content {
          role = message.role.text
          text(it.content)
        }
      }
    }
    generativeChat.value?.history?.addAll(content)
  }

  override fun getMessageStream(roomId: String): Flow<PagingData<Message>> {
    return Pager(config = PagingConfig(pageSize = MESSAGE_PAGE_SIZE)) {
      messageDao.getMessagePagingSource(roomId)
    }.flow.map { pagingData -> pagingData.map { it.toDomain() } }
  }

  private suspend fun insertUserTextMessage(message: String, roomId: String) {
    messageDao.insert(
      Message(
        roomId = roomId,
        content = TextContent(message),
        role = Role.User,
      ).toEntity(),
    )
  }

  override suspend fun sendTextMessage(message: String, id: String): Flow<GenerateContentResponse> {
    val roomId = this.currentRoomId.value ?: throwJoinNotCalledError()
    val chat = this.generativeChat.value ?: throwJoinNotCalledError()

    insertUserTextMessage(message = message, roomId = roomId)

    val content = content {
      role = Role.User.text
      text(message)
    }
    val responseText = StringBuilder()
    return chat
      .sendMessageStream(content)
      .onEach { response ->
        responseText.append(response.text)
      }.onCompletion {
        if (responseText.isNotEmpty()) {
          messageDao.insert(
            Message(
              id = id,
              roomId = roomId,
              content = TextContent(responseText.toString()),
              role = Role.Model,
            ).toEntity(),
          )
        }
      }
  }

  private fun throwJoinNotCalledError(): Nothing {
    error("Call join before using sendTextMessage")
  }

  override fun exit() {
    currentRoomId.update { null }
    generativeModel.update { null }
  }

  companion object {
    private const val MESSAGE_PAGE_SIZE = 30
  }
}
