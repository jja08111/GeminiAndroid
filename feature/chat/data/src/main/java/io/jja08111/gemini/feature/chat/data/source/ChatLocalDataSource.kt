package io.jja08111.gemini.feature.chat.data.source

import android.graphics.Bitmap
import io.jja08111.gemini.database.dao.MessageDao
import io.jja08111.gemini.database.dao.RoomDao
import io.jja08111.gemini.database.entity.ModelResponseEntity
import io.jja08111.gemini.database.entity.ModelResponseStateEntity
import io.jja08111.gemini.database.entity.PromptEntity
import io.jja08111.gemini.database.entity.PromptImageEntity
import io.jja08111.gemini.database.entity.RoomEntity
import io.jja08111.gemini.database.entity.partial.ModelResponseContentPartial
import io.jja08111.gemini.database.entity.relation.PromptWithImages
import io.jja08111.gemini.database.extension.toDomain
import io.jja08111.gemini.feature.chat.data.exception.MessageInsertionException
import io.jja08111.gemini.feature.chat.data.exception.RoomInsertionException
import io.jja08111.gemini.feature.chat.data.extension.convertToMessageGroups
import io.jja08111.gemini.model.MessageGroup
import io.jja08111.gemini.model.ModelResponse
import io.jja08111.gemini.model.Prompt
import io.jja08111.gemini.model.createId
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatLocalDataSource @Inject constructor(
  private val promptImageLocalDataSource: PromptImageLocalDataSource,
  private val messageDao: MessageDao,
  private val roomDao: RoomDao,
) {
  fun completePendingMessagesState() {
    messageDao.updateAllModelResponseState(
      oldState = ModelResponseStateEntity.Generating,
      newState = ModelResponseStateEntity.Generated,
    )
  }

  fun getMessageGroupStream(roomId: String): Flow<List<MessageGroup>> {
    return messageDao.getPromptWithResponsesAndImages(roomId).mapLatest(::convertToMessageGroups)
  }

  suspend fun getMessageGroupsBy(roomId: String): List<MessageGroup> {
    val messageGroupStream = getMessageGroupStream(roomId)
    return messageGroupStream.first()
  }

  suspend fun updateResponseContentPartials(partials: List<ModelResponseContentPartial>) {
    messageDao.updateAll(partials)
  }

  suspend fun updateResponseStatesBy(promptId: String, state: ModelResponseStateEntity) {
    messageDao.updateResponseStatesBy(promptId = promptId, state = state)
  }

  suspend fun insertInitialMessageGroup(
    promptId: String,
    roomId: String,
    parentModelResponseId: String?,
    prompt: String,
    imageBitmaps: List<Bitmap>,
    responseIds: List<String>,
  ) {
    val now = LocalDateTime.now()
    val savedImagePathAndBitmaps = coroutineScope {
      val deferred = imageBitmaps.map { bitmap ->
        async {
          val path = promptImageLocalDataSource.saveImage(bitmap)
          path to bitmap
        }
      }
      return@coroutineScope deferred.awaitAll()
    }
    val images = savedImagePathAndBitmaps.map { (path: String, bitmap: Bitmap) ->
      PromptImageEntity(
        id = createId(),
        promptId = promptId,
        path = path,
        width = bitmap.width,
        height = bitmap.height,
      )
    }

    try {
      messageDao.insert(
        prompt = PromptEntity(
          id = promptId,
          roomId = roomId,
          parentModelResponseId = parentModelResponseId,
          text = prompt,
          createdAt = now,
        ),
        images = images,
        modelResponses = responseIds.mapIndexed { index, id ->
          ModelResponseEntity(
            id = id,
            roomId = roomId,
            parentPromptId = promptId,
            text = "",
            createdAt = now,
            state = ModelResponseStateEntity.Generating,
            selected = index == 0,
          )
        },
      )
    } catch (e: Exception) {
      throw MessageInsertionException(cause = e)
    }
  }

  suspend fun insertRoom(roomId: String, title: String) {
    try {
      roomDao.insert(RoomEntity(id = roomId, createdAt = LocalDateTime.now(), title = title))
    } catch (e: Exception) {
      throw RoomInsertionException(cause = e)
    }
  }

  suspend fun insertResponsesAndRemoveError(
    newResponseIds: List<String>,
    errorResponseId: String,
    roomId: String,
    promptId: String,
  ) {
    try {
      val responses = newResponseIds.mapIndexed { index, id ->
        ModelResponseEntity(
          id = id,
          roomId = roomId,
          parentPromptId = promptId,
          text = "",
          createdAt = LocalDateTime.now(),
          state = ModelResponseStateEntity.Generating,
          selected = index == 0,
        )
      }
      messageDao.insertAndRemove(modelResponses = responses, responseIdToRemove = errorResponseId)
    } catch (e: Exception) {
      throw MessageInsertionException(cause = e)
    }
  }

  suspend fun insertAndUnselectOldResponses(
    newResponseIds: List<String>,
    roomId: String,
    promptId: String,
  ) {
    try {
      val responses = newResponseIds.mapIndexed { index, id ->
        ModelResponseEntity(
          id = id,
          roomId = roomId,
          parentPromptId = promptId,
          text = "",
          createdAt = LocalDateTime.now(),
          state = ModelResponseStateEntity.Generating,
          selected = index == 0,
        )
      }
      messageDao.insertAndUnselectOldResponses(modelResponses = responses)
    } catch (e: Exception) {
      throw MessageInsertionException(cause = e)
    }
  }

  fun getPromptBy(promptId: String): Flow<Prompt> {
    return messageDao.getPromptWithImages(promptId = promptId).map(PromptWithImages::toDomain)
  }

  fun getModelResponsesBy(parentPromptId: String): Flow<List<ModelResponse>> {
    return messageDao.getModelResponses(parentPromptId = parentPromptId)
      .map {
        it.map(ModelResponseEntity::toDomain)
      }
  }

  suspend fun changeSelectedResponse(promptId: String, responseId: String) {
    messageDao.changeSelectedResponse(promptId = promptId, newSelectedResponseId = responseId)
  }
}
