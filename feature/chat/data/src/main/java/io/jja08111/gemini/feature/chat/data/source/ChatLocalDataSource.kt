package io.jja08111.gemini.feature.chat.data.source

import io.jja08111.gemini.database.dao.MessageDao
import io.jja08111.gemini.database.dao.RoomDao
import io.jja08111.gemini.database.entity.ModelResponseEntity
import io.jja08111.gemini.database.entity.ModelResponseStateEntity
import io.jja08111.gemini.database.entity.PromptEntity
import io.jja08111.gemini.database.entity.RoomEntity
import io.jja08111.gemini.database.entity.partial.ModelResponseContentPartial
import io.jja08111.gemini.feature.chat.data.extension.convertToMessageGroups
import io.jja08111.gemini.model.MessageGroup
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatLocalDataSource @Inject constructor(
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
    return messageDao.getPromptAndResponses(roomId).mapLatest(::convertToMessageGroups)
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
    responseIds: List<String>,
  ) {
    val now = Date().time
    messageDao.insert(
      PromptEntity(
        id = promptId,
        roomId = roomId,
        parentModelResponseId = parentModelResponseId,
        text = prompt,
        createdAt = now,
      ),
      responseIds.mapIndexed { index, id ->
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
  }

  suspend fun insertRoom(roomId: String, title: String) {
    roomDao.insert(RoomEntity(id = roomId, createdAt = Date().time, title = title))
  }
}
