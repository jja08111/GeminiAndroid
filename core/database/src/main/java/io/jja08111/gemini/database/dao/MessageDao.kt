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
interface MessageDao {
  @Query(
    """
      SELECT * 
      FROM prompt JOIN model_response ON prompt.id = model_response.parent_prompt_id
      WHERE prompt.room_id = :roomId ORDER BY prompt.created_at ASC
    """,
  )
  fun getPromptAndResponses(roomId: String): Flow<Map<PromptEntity, List<ModelResponseEntity>>>

  @Insert
  suspend fun insert(prompt: PromptEntity, modelResponse: List<ModelResponseEntity>)

  @Update(entity = ModelResponseEntity::class)
  suspend fun updateAll(message: List<ModelResponseContentPartial>)

  @Transaction
  @Query(
    """
      UPDATE model_response
      SET state = :state
      WHERE parent_prompt_id = :promptId
    """,
  )
  suspend fun updateResponseStatesBy(promptId: String, state: ModelResponseStateEntity)

  @Query("UPDATE model_response SET state = :newState WHERE state = :oldState")
  fun updateAllModelResponseState(
    oldState: ModelResponseStateEntity,
    newState: ModelResponseStateEntity,
  )
}
