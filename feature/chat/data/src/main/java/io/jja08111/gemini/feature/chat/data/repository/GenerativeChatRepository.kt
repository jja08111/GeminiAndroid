package io.jja08111.gemini.feature.chat.data.repository

import android.graphics.Bitmap
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.Content
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import io.github.jja08111.core.common.di.IoDispatcher
import io.github.jja08111.core.common.image.BitmapCreator
import io.jja08111.gemini.database.entity.ModelResponseStateEntity
import io.jja08111.gemini.feature.chat.data.BuildConfig
import io.jja08111.gemini.feature.chat.data.exception.EmptyContentException
import io.jja08111.gemini.feature.chat.data.exception.EmptyMessageGroupsException
import io.jja08111.gemini.feature.chat.data.exception.NotJoinedException
import io.jja08111.gemini.feature.chat.data.extension.toContents
import io.jja08111.gemini.feature.chat.data.extension.toResponseContentPartials
import io.jja08111.gemini.feature.chat.data.model.AttachedImage
import io.jja08111.gemini.feature.chat.data.model.CANDIDATE_COUNT
import io.jja08111.gemini.feature.chat.data.model.MODEL_NAME
import io.jja08111.gemini.feature.chat.data.model.ROLE_USER
import io.jja08111.gemini.feature.chat.data.model.ResponseTextBuilder
import io.jja08111.gemini.feature.chat.data.source.ChatLocalDataSource
import io.jja08111.gemini.feature.chat.data.source.PromptImageLocalDataSource
import io.jja08111.gemini.model.MessageGroup
import io.jja08111.gemini.model.createId
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

class GenerativeChatRepository @Inject constructor(
  @IoDispatcher externalDispatcher: CoroutineDispatcher,
  private val chatLocalDataSource: ChatLocalDataSource,
  private val promptImageLocalDataSource: PromptImageLocalDataSource,
  private val bitmapCreator: BitmapCreator,
) : ChatRepository {
  private val coroutineScope = CoroutineScope(SupervisorJob() + externalDispatcher)
  private var joinedRoomId: String? = null
  private var generativeModel: GenerativeModel? = null

  init {
    coroutineScope.launch {
      // Update state of all messages because the Generating state is still remain when app is killed.
      chatLocalDataSource.completePendingMessagesState()
    }
  }

  override fun join(roomId: String): Flow<List<MessageGroup>> {
    joinedRoomId = roomId
    generativeModel = GenerativeModel(
      modelName = MODEL_NAME,
      apiKey = BuildConfig.GEMINI_API_KEY,
      generationConfig = generationConfig {
        candidateCount = CANDIDATE_COUNT
      },
    )
    return chatLocalDataSource.getMessageGroupStream(roomId)
  }

  override suspend fun sendMessage(
    message: String,
    images: List<AttachedImage>,
    onRoomCreated: (Flow<List<MessageGroup>>) -> Unit,
  ): Result<Unit> {
    return runCatching {
      val roomId = joinedRoomId ?: throw NotJoinedException()
      val model = generativeModel ?: throw NotJoinedException()
      val messageGroupStream = chatLocalDataSource.getMessageGroupStream(roomId)
      val messageGroups = messageGroupStream.first()
      val isNewChat = messageGroups.isEmpty()

      if (isNewChat) {
        val title = when {
          message.isNotEmpty() -> message
          images.isNotEmpty() -> "Image question"
          else -> throw EmptyContentException()
        }
        chatLocalDataSource.insertRoom(roomId = roomId, title = title)
        onRoomCreated(messageGroupStream)
      }

      val promptId = createId()
      val responseTextBuilders = List(CANDIDATE_COUNT) { ResponseTextBuilder() }
      val parentModelResponseId = messageGroups.lastOrNull()?.selectedResponse?.id
      val imageBitmaps = images.map {
        when (it) {
          is AttachedImage.Bitmap -> it.bitmap
          is AttachedImage.Uri -> bitmapCreator.create(it.uri)
        }
      }

      chatLocalDataSource.insertInitialMessageGroup(
        prompt = message,
        imageBitmaps = imageBitmaps,
        roomId = roomId,
        promptId = promptId,
        responseIds = responseTextBuilders.map { it.id },
        parentModelResponseId = parentModelResponseId,
      )

      return model.generateTextMessageStream(
        message = message,
        images = imageBitmaps,
        history = messageGroups.flatMap(MessageGroup::toContents),
        promptId = promptId,
        responseTextBuilders = responseTextBuilders,
      )
    }
  }

  override suspend fun regenerateOnError(): Result<Unit> {
    return runCatching {
      val model = generativeModel ?: throw NotJoinedException()
      val roomId = joinedRoomId ?: throw NotJoinedException()
      val messageGroups = chatLocalDataSource.getMessageGroupsBy(roomId)
      val lastMessageGroup = messageGroups.lastOrNull() ?: throw EmptyMessageGroupsException()
      val lastPrompt = lastMessageGroup.prompt
      val responseTextBuilders = List(CANDIDATE_COUNT) { ResponseTextBuilder() }

      chatLocalDataSource.insertResponsesAndRemoveError(
        newResponseIds = responseTextBuilders.map { it.id },
        errorResponseId = lastMessageGroup.selectedResponse.id,
        roomId = roomId,
        promptId = lastPrompt.id,
      )

      return model.generateTextMessageStream(
        message = lastPrompt.text,
        images = lastPrompt.images.map { promptImageLocalDataSource.loadImage(it.path) },
        history = messageGroups
          .dropLast(1)
          .flatMap(MessageGroup::toContents),
        promptId = lastPrompt.id,
        responseTextBuilders = responseTextBuilders,
      )
    }
  }

  override suspend fun regenerateResponse(responseId: String): Result<Unit> {
    return runCatching {
      val model = generativeModel ?: throw NotJoinedException()
      val roomId = joinedRoomId ?: throw NotJoinedException()
      val messageGroups = chatLocalDataSource.getMessageGroupsBy(roomId)
      val messageGroup = messageGroups.firstOrNull {
        it.selectedResponse.id == responseId
      } ?: throw EmptyMessageGroupsException()
      val prompt = messageGroup.prompt
      val responseTextBuilders = List(CANDIDATE_COUNT) { ResponseTextBuilder() }

      chatLocalDataSource.insertAndUnselectOldResponses(
        newResponseIds = responseTextBuilders.map { it.id },
        roomId = roomId,
        promptId = prompt.id,
      )

      return model.generateTextMessageStream(
        message = prompt.text,
        images = prompt.images.map { promptImageLocalDataSource.loadImage(it.path) },
        history = messageGroups.flatMap(MessageGroup::toContents),
        promptId = prompt.id,
        responseTextBuilders = responseTextBuilders,
      )
    }
  }

  private suspend fun GenerativeModel.generateTextMessageStream(
    message: String,
    images: List<Bitmap>,
    history: List<Content>,
    promptId: String,
    responseTextBuilders: List<ResponseTextBuilder>,
  ): Result<Unit> {
    return suspendCancellableCoroutine { continuation ->
      coroutineScope.launch {
        val prompt = content {
          role = ROLE_USER
          images.forEach(::image)
          text(message)
        }

        generateContentStream(*history.toTypedArray(), prompt)
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

  override fun exit() {
    joinedRoomId = null
    generativeModel = null
  }
}
