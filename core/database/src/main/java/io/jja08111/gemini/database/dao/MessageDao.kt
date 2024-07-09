package io.jja08111.gemini.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import io.jja08111.gemini.database.entity.ModelResponseEntity
import io.jja08111.gemini.database.entity.ModelResponseStateEntity
import io.jja08111.gemini.database.entity.PromptEntity
import io.jja08111.gemini.database.entity.partial.ModelResponseContentPartial
import kotlinx.coroutines.flow.Flow

@Dao
abstract class MessageDao {
  @Query(
    """
      SELECT * 
      FROM prompt JOIN model_response ON prompt.id = model_response.parent_prompt_id
      WHERE prompt.room_id = :roomId ORDER BY prompt.created_at ASC
    """,
  )
  abstract fun getPromptAndResponses(
    roomId: String,
  ): Flow<Map<PromptEntity, List<ModelResponseEntity>>>

  @Query(
    """
      SELECT *
      FROM prompt
      WHERE prompt.id = :promptId
    """,
  )
  abstract fun getPrompt(promptId: String): Flow<PromptEntity>

  @Query(
    """
      SELECT *
      FROM model_response
      WHERE model_response.parent_prompt_id = :parentPromptId
    """,
  )
  abstract fun getModelResponses(parentPromptId: String): Flow<List<ModelResponseEntity>>

  @Insert
  abstract suspend fun insert(prompt: PromptEntity, modelResponse: List<ModelResponseEntity>)

  @Insert
  abstract suspend fun insert(modelResponses: List<ModelResponseEntity>)

  @Transaction
  open suspend fun insertAndRemove(
    modelResponses: List<ModelResponseEntity>,
    responseIdToRemove: String,
  ) {
    insert(modelResponses)
    deleteResponseById(responseIdToRemove)
  }

  @Transaction
  open suspend fun insertAndUnselectOldResponses(modelResponses: List<ModelResponseEntity>) {
    require(modelResponses.isNotEmpty())
    unselectResponseByParentPromptId(modelResponses.first().parentPromptId)
    insert(modelResponses)
  }

  @Transaction
  open suspend fun changeSelectedResponse(promptId: String, newSelectedResponseId: String) {
    unselectResponseByParentPromptId(promptId)
    selectModelResponse(newSelectedResponseId)
  }

  @Query(
    """
      UPDATE model_response
      SET selected = 0
      WHERE parent_prompt_id = :promptId
    """,
  )
  abstract suspend fun unselectResponseByParentPromptId(promptId: String)

  @Update(entity = ModelResponseEntity::class)
  abstract suspend fun updateAll(message: List<ModelResponseContentPartial>)

  @Transaction
  @Query(
    """
      UPDATE model_response
      SET state = :state
      WHERE parent_prompt_id = :promptId
    """,
  )
  abstract suspend fun updateResponseStatesBy(promptId: String, state: ModelResponseStateEntity)

  @Query("UPDATE model_response SET state = :newState WHERE state = :oldState")
  abstract fun updateAllModelResponseState(
    oldState: ModelResponseStateEntity,
    newState: ModelResponseStateEntity,
  )

  @Query(
    """
        UPDATE model_response
        SET selected = 1
        WHERE id = :responseId
    """,
  )
  abstract suspend fun selectModelResponse(responseId: String)

  @Query("DELETE FROM model_response WHERE id = :id")
  abstract suspend fun deleteResponseById(id: String)
}
