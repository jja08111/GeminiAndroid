package io.jja08111.gemini.feature.chat.data.repository

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import io.jja08111.gemini.database.dao.MessageDao
import io.jja08111.gemini.database.entity.MessageContentEntity
import io.jja08111.gemini.database.entity.MessageStateEntity
import io.jja08111.gemini.database.extension.toDomain
import io.jja08111.gemini.database.extension.toEntity
import io.jja08111.gemini.feature.chat.data.BuildConfig
import io.jja08111.gemini.model.Message
import io.jja08111.gemini.model.MessageState
import io.jja08111.gemini.model.Role
import io.jja08111.gemini.model.TextContent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.Date
import java.util.UUID
import javax.inject.Inject
import kotlin.coroutines.resume

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

  init {
    coroutineScope.launch {
      // Update state of all messages because the Generating state is still remain when app is killed.
      messageDao.updateAllMessagesState(
        oldState = MessageState.Generating,
        newState = MessageState.Success,
      )
    }
  }

  override fun join(roomId: String): Flow<List<Message>> {
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
    val messageStream = messageDao.getAllMessages(roomId).map { stream ->
      stream.map { it.toDomain() }
    }
    coroutineScope.launch {
      val contents = messageStream.firstOrNull()?.map { message ->
        when (val content = message.content) {
          is TextContent -> content {
            role = message.role.text
            text(content.text)
          }
        }
      }
      if (contents != null) {
        generativeChat.value?.history?.addAll(contents)
      }
    }
    return messageStream
  }

  override suspend fun sendTextMessage(message: String, id: String): Result<Unit> {
    val chat = generativeChat.value ?: throwJoinNotCalledError()
    val content = content {
      role = Role.User.text
      text(message)
    }
    val responseText = StringBuilder()

    insertUserAndModelInitialMessages(message = message, modelMessageId = id)

    return suspendCancellableCoroutine { continuation ->
      coroutineScope.launch {
        chat.sendMessageStream(content)
          .onEach { response ->
            if (response.text != null) {
              responseText.append(response.text)
              val messageContent = MessageContentEntity(id = id, content = responseText.toString())
              messageDao.update(messageContent)
            }
          }
          .onCompletion { throwable ->
            val isError = throwable != null
            val messageState = MessageStateEntity(
              id = id,
              state = if (isError) MessageState.Error else MessageState.Success,
            )
            messageDao.update(messageState)
            val result = if (throwable != null) Result.failure(throwable) else Result.success(Unit)
            continuation.resume(result)
          }
          .catch { /* Handling exception at the `onCompletion` */ }
          .collect()
      }
    }
  }

  private suspend fun insertUserAndModelInitialMessages(message: String, modelMessageId: String) {
    val roomId = currentRoomId.value ?: throwJoinNotCalledError()
    val userMessageId = UUID.randomUUID().toString()
    val createdAt = Date()

    messageDao.insert(
      Message(
        id = userMessageId,
        roomId = roomId,
        content = TextContent(message),
        role = Role.User,
        // Hardcoding minus to place user message front of model message.
        createdAt = Date(createdAt.time - 1),
        state = MessageState.Success,
      ).toEntity(),
      Message(
        id = modelMessageId,
        roomId = roomId,
        role = Role.Model,
        content = TextContent(text = ""),
        createdAt = createdAt,
        state = MessageState.Generating,
      ).toEntity(),
    )
  }

  private fun throwJoinNotCalledError(): Nothing {
    error("Call join before using sendTextMessage")
  }

  override fun exit() {
    currentRoomId.update { null }
    generativeModel.update { null }
  }
}
