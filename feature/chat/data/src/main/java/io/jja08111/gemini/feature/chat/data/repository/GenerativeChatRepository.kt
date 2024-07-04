package io.jja08111.gemini.feature.chat.data.repository

import com.google.ai.client.generativeai.Chat
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.Content
import com.google.ai.client.generativeai.type.GenerateContentResponse
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import io.github.jja08111.core.common.di.IoDispatcher
import io.jja08111.gemini.database.entity.ModelResponseStateEntity
import io.jja08111.gemini.feature.chat.data.BuildConfig
import io.jja08111.gemini.feature.chat.data.extension.toContents
import io.jja08111.gemini.feature.chat.data.extension.toResponseContentPartials
import io.jja08111.gemini.feature.chat.data.model.CANDIDATE_COUNT
import io.jja08111.gemini.feature.chat.data.model.MODEL_NAME
import io.jja08111.gemini.feature.chat.data.model.ROLE_USER
import io.jja08111.gemini.feature.chat.data.model.ResponseTextBuilder
import io.jja08111.gemini.feature.chat.data.source.ChatLocalDataSource
import io.jja08111.gemini.model.MessageGroup
import io.jja08111.gemini.model.createId
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

class GenerativeChatRepository @Inject constructor(
  @IoDispatcher externalDispatcher: CoroutineDispatcher,
  private val chatLocalDataSource: ChatLocalDataSource,
) : ChatRepository {
  private val coroutineScope = CoroutineScope(SupervisorJob() + externalDispatcher)
  private var currentRoomId: String? = null
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
      chatLocalDataSource.completePendingMessagesState()
    }
  }

  override fun join(roomId: String): Flow<List<MessageGroup>> {
    currentRoomId = roomId
    generativeModel.update {
      GenerativeModel(
        modelName = MODEL_NAME,
        apiKey = BuildConfig.GEMINI_API_KEY,
        generationConfig = generationConfig {
          candidateCount = CANDIDATE_COUNT
        },
      )
    }
    return try {
      chatLocalDataSource.getMessageGroupStream(roomId)
    } catch (e: Exception) {
      flowOf(emptyList())
    }
  }

  override suspend fun sendTextMessage(
    message: String,
    onRoomCreated: (Flow<List<MessageGroup>>) -> Unit,
  ): Result<Unit> {
    val roomId = currentRoomId ?: throwNotJoinedError()
    val messageGroupStream = chatLocalDataSource.getMessageGroupStream(roomId)
    val messageGroups = messageGroupStream.first()
    val isNewChat = messageGroups.isEmpty()

    if (isNewChat) {
      chatLocalDataSource.insertRoom(roomId = roomId, title = message)
      onRoomCreated(messageGroupStream)
    }

    val promptId = createId()
    val responseTextBuilders = List(CANDIDATE_COUNT) { ResponseTextBuilder() }
    val parentModelResponseId = messageGroups.lastOrNull()?.selectedResponse?.id

    chatLocalDataSource.insertInitialMessageGroup(
      prompt = message,
      roomId = roomId,
      promptId = promptId,
      responseIds = responseTextBuilders.map { it.id },
      parentModelResponseId = parentModelResponseId,
    )

    return suspendCancellableCoroutine { continuation ->
      coroutineScope.launch {
        val chat = generativeChat.value ?: throwNotJoinedError()
        val history = messageGroups.flatMap(MessageGroup::toContents)
        val prompt = content {
          role = ROLE_USER
          text(message)
        }

        chat.sendMessageStreamWithNewHistory(prompt, history)
          .onEach { response ->
            val candidates = response.candidates
            val contentPartials = candidates.toResponseContentPartials(responseTextBuilders)
            chatLocalDataSource.updateResponseContentPartials(contentPartials)
          }
          .onCompletion { throwable ->
            val isError = throwable != null
            val state = if (isError) {
              ModelResponseStateEntity.Error
            } else {
              ModelResponseStateEntity.Generated
            }
            chatLocalDataSource.updateResponseStatesBy(promptId = promptId, state = state)
            val result = if (throwable != null) Result.failure(throwable) else Result.success(Unit)
            continuation.resume(result)
          }
          .catch { /* Handling exception at the `onCompletion` */ }
          .collect()
      }
    }
  }

  private fun Chat.sendMessageStreamWithNewHistory(
    prompt: Content,
    history: List<Content>,
  ): Flow<GenerateContentResponse> {
    this.history.clear()
    this.history.addAll(history)
    return sendMessageStream(prompt)
  }

  private fun throwNotJoinedError(): Nothing {
    error("Must call join function before usage")
  }

  override fun exit() {
    currentRoomId = null
    generativeModel.update { null }
  }
}
