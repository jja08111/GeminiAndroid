package io.jja08111.gemini.feature.chat.data.repository

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import io.github.jja08111.core.common.di.IoDispatcher
import io.jja08111.gemini.database.dao.MessageDao
import io.jja08111.gemini.database.entity.ModelResponseEntity
import io.jja08111.gemini.database.entity.ModelResponseStateEntity
import io.jja08111.gemini.database.entity.PromptEntity
import io.jja08111.gemini.feature.chat.data.BuildConfig
import io.jja08111.gemini.feature.chat.data.extension.convertToMessageGroups
import io.jja08111.gemini.feature.chat.data.extension.toContents
import io.jja08111.gemini.feature.chat.data.extension.toResponseContentPartials
import io.jja08111.gemini.feature.chat.data.model.CANDIDATE_COUNT
import io.jja08111.gemini.feature.chat.data.model.ROLE_USER
import io.jja08111.gemini.feature.chat.data.model.ResponseTextBuilder
import io.jja08111.gemini.model.MessageGroup
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
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
  @IoDispatcher externalDispatcher: CoroutineDispatcher,
  private val messageDao: MessageDao,
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
      messageDao.updateAllModelResponseState(
        oldState = ModelResponseStateEntity.Generating,
        newState = ModelResponseStateEntity.Generated,
      )
    }
  }

  override fun join(roomId: String): Flow<List<MessageGroup>> {
    currentRoomId = roomId
    generativeModel.update {
      GenerativeModel(
        modelName = "gemini-pro",
        apiKey = BuildConfig.GEMINI_API_KEY,
        generationConfig = generationConfig {
          candidateCount = CANDIDATE_COUNT
        },
      )
    }
    return messageDao.getPromptAndResponses(roomId).mapLatest(::convertToMessageGroups)
  }

  override suspend fun sendTextMessage(
    message: String,
    messageGroups: List<MessageGroup>,
    parentModelResponseId: String?,
  ): Result<Unit> {
    val chat = generativeChat.value ?: throwJoinNotCalledError()
    val content = content {
      role = ROLE_USER
      text(message)
    }
    val promptId = UUID.randomUUID().toString()
    val responseTextBuilders = List(CANDIDATE_COUNT) { ResponseTextBuilder() }

    insertInitialMessageGroup(
      prompt = message,
      promptId = promptId,
      responseIds = responseTextBuilders.map { it.id },
      parentModelResponseId = parentModelResponseId,
    )

    val history = messageGroups.flatMap(MessageGroup::toContents)
    chat.history.clear()
    chat.history.addAll(history)

    return suspendCancellableCoroutine { continuation ->
      coroutineScope.launch {
        chat.sendMessageStream(content)
          .onEach { response ->
            // TODO: 생성되고 있는 응답은 UI에서 컨트롤 하기. DB에 값을 갱신하는 것은 부하가 크고 UI 모델을 다시 생성하게 함
            val candidates = response.candidates
            val contentPartials = candidates.toResponseContentPartials(responseTextBuilders)
            messageDao.updateAll(contentPartials)
          }
          .onCompletion { throwable ->
            val isError = throwable != null
            val state = if (isError) {
              ModelResponseStateEntity.Error
            } else {
              ModelResponseStateEntity.Generated
            }
            messageDao.updateResponseStatesBy(promptId = promptId, state = state)
            val result = if (throwable != null) Result.failure(throwable) else Result.success(Unit)
            continuation.resume(result)
          }
          .catch { /* Handling exception at the `onCompletion` */ }
          .collect()
      }
    }
  }

  private suspend fun insertInitialMessageGroup(
    prompt: String,
    promptId: String,
    responseIds: List<String>,
    parentModelResponseId: String?,
  ) {
    val roomId = currentRoomId ?: throwJoinNotCalledError()
    val createdAt = Date().time

    messageDao.insert(
      PromptEntity(
        id = promptId,
        roomId = roomId,
        parentModelResponseId = parentModelResponseId,
        text = prompt,
        createdAt = createdAt,
      ),
      responseIds.mapIndexed { index, id ->
        ModelResponseEntity(
          id = id,
          roomId = roomId,
          parentPromptId = promptId,
          text = "",
          createdAt = createdAt,
          state = ModelResponseStateEntity.Generating,
          selected = index == 0,
        )
      },
    )
  }

  private fun throwJoinNotCalledError(): Nothing {
    error("Must call join function before usage")
  }

  override fun exit() {
    currentRoomId = null
    generativeModel.update { null }
  }
}
