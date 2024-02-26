package io.jja08111.gemini.feature.chat.data.repository

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.asTextOrNull
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import io.jja08111.gemini.database.dao.MessageDao
import io.jja08111.gemini.database.entity.ModelResponseEntity
import io.jja08111.gemini.database.entity.ModelResponseStateEntity
import io.jja08111.gemini.database.entity.PromptEntity
import io.jja08111.gemini.database.entity.partial.ModelResponsePartial
import io.jja08111.gemini.database.extension.toDomain
import io.jja08111.gemini.feature.chat.data.BuildConfig
import io.jja08111.gemini.feature.chat.data.extension.toContents
import io.jja08111.gemini.feature.chat.data.model.CANDIDATE_COUNT
import io.jja08111.gemini.feature.chat.data.model.MessageResponse
import io.jja08111.gemini.feature.chat.data.model.MessageResponseData
import io.jja08111.gemini.feature.chat.data.model.MessageResponseFinished
import io.jja08111.gemini.feature.chat.data.model.ROLE_USER
import io.jja08111.gemini.model.MessageGroup
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
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
      messageDao.updateAllModelResponseState(
        oldState = ModelResponseStateEntity.Generating,
        newState = ModelResponseStateEntity.Generated,
      )
    }
  }

  override fun join(roomId: String): Flow<List<MessageGroup>> {
    currentRoomId.update { roomId }
    generativeModel.update {
      GenerativeModel(
        modelName = "gemini-pro",
        apiKey = BuildConfig.GEMINI_API_KEY,
        generationConfig = generationConfig {
          candidateCount = CANDIDATE_COUNT
        },
      )
    }
    return messageDao.getPromptAndResponses(roomId).mapLatest { promptAndMessages ->
      val result = mutableListOf<MessageGroup>()
      promptAndMessages.forEach { (prompt, responses) ->
        val lastResponse = result.lastOrNull()?.selectedResponse
        if (result.isEmpty() || prompt.parentModelResponseId == lastResponse?.id) {
          val messageGroup = MessageGroup(
            prompt = prompt.toDomain(),
            selectedResponse = responses.find { it.selected }?.toDomain() ?: error(
              "There is no selected response.",
            ),
            responseCount = responses.size,
          )
          result.add(messageGroup)
        }
      }
      result
    }
  }

  override suspend fun sendTextMessage(
    message: String,
    messageGroups: List<MessageGroup>,
    parentModelResponseId: String?,
  ): Flow<MessageResponse> {
    val chat = generativeChat.value ?: throwJoinNotCalledError()
    val content = content {
      role = ROLE_USER
      text(message)
    }
    val promptId = UUID.randomUUID().toString()
    val responseHolders = List(CANDIDATE_COUNT) { ModelResponseHolder() }

    insertInitialPromptAndResponses(
      prompt = message,
      promptId = promptId,
      responseIds = responseHolders.map { it.id },
      parentModelResponseId = parentModelResponseId,
    )

    val history = messageGroups.flatMap { it.toContents() }
    chat.history.clear()
    chat.history.addAll(history)

    return suspendCancellableCoroutine { continuation ->
      val responseFlow = MutableSharedFlow<MessageResponse>()
      continuation.resume(responseFlow)

      coroutineScope.launch {
        chat.sendMessageStream(content)
          .onEach { response ->
            responseFlow.emit(MessageResponseData(response))
            response.candidates.mapIndexedNotNull map@{ index, candidate ->
              val responseHolder = responseHolders[index]
              val parts = candidate.content.parts
              val text = parts.firstOrNull()?.asTextOrNull() ?: return@map null
              val responseTextBuilder = responseHolder.textBuilder
              responseTextBuilder.append(text)
            }
          }
          .onCompletion { throwable ->
            val isError = throwable != null
            val state = if (isError) {
              ModelResponseStateEntity.Error
            } else {
              ModelResponseStateEntity.Generated
            }
            val modelResponsePartials = responseHolders.map { holder ->
              ModelResponsePartial(
                id = holder.id,
                text = holder.textBuilder.toString(),
                state = state,
              )
            }
            messageDao.updateAll(modelResponsePartials)
            responseFlow.emit(MessageResponseFinished)
          }
          .collect()
      }
    }
  }

  private suspend fun insertInitialPromptAndResponses(
    prompt: String,
    promptId: String,
    responseIds: List<String>,
    parentModelResponseId: String?,
  ) {
    val roomId = currentRoomId.value ?: throwJoinNotCalledError()
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
    error("Call join before using sendTextMessage")
  }

  override fun exit() {
    currentRoomId.update { null }
    generativeModel.update { null }
  }
}

private data class ModelResponseHolder(
  val id: String = UUID.randomUUID().toString(),
  val textBuilder: StringBuilder = StringBuilder(),
)
