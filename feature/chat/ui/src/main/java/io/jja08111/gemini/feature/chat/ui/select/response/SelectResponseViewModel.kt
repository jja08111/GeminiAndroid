package io.jja08111.gemini.feature.chat.ui.select.response

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jja08111.core.navigation.mobile.SelectResponseDestinations
import io.jja08111.gemini.core.ui.Message
import io.jja08111.gemini.feature.chat.data.repository.HistoryRepository
import io.jja08111.gemini.feature.chat.ui.R
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class SelectResponseViewModel @Inject constructor(
  savedStateHandle: SavedStateHandle,
  private val historyRepository: HistoryRepository,
) : ViewModel(), ContainerHost<SelectResponseUiState, SelectResponseSideEffect> {
  private val promptId = savedStateHandle.get<String>(SelectResponseDestinations.PROMPT_ID_ARG)
    ?: error("Missing promptId argument from NavHost.")

  override val container: Container<SelectResponseUiState, SelectResponseSideEffect> = container(
    SelectResponseUiState(
      promptStream = historyRepository.getPromptFlow(promptId = promptId),
      responsesStream = historyRepository.getResponsesFlow(parentPromptId = promptId),
    ),
  )

  fun changeSelectedResponse(responseId: String) {
    intent {
      historyRepository.changeSelectedResponse(promptId = promptId, responseId = responseId)
        .onSuccess {
          postSideEffect(SelectResponseSideEffect.PopBackStack)
        }
        .onFailure {
          postSideEffect(
            SelectResponseSideEffect.UserMessage(
              message = Message.Resource(R.string.feature_chat_ui_unknown_error_message),
            ),
          )
        }
    }
  }
}
