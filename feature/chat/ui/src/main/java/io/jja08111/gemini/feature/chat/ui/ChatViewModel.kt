package io.jja08111.gemini.feature.chat.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.map
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jja08111.core.navigation.mobile.ChatMobileDestinations
import io.jja08111.gemini.feature.chat.data.repository.ChatRepository
import io.jja08111.gemini.model.Message
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.annotation.OrbitExperimental
import org.orbitmvi.orbit.syntax.simple.blockingIntent
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
  savedStateHandle: SavedStateHandle,
  private val chatRepository: ChatRepository,
) : ViewModel(), ContainerHost<ChatUiState, ChatSideEffect> {
  private val roomId = savedStateHandle.get<String>(
    ChatMobileDestinations.CHAT_ID_ARG,
  ) ?: error("Missing chatId argument from the NavHost.")

  override val container = container<ChatUiState, ChatSideEffect>(
    ChatUiState(
      messageStream = chatRepository.getMessageStream(roomId).observeGeneratingEnd(),
    ),
  )

  init {
    viewModelScope.launch {
      chatRepository.join(roomId)
    }
  }

  private fun Flow<PagingData<Message>>.observeGeneratingEnd(): Flow<PagingData<Message>> {
    return this.map { pagingData ->
      pagingData.map { message ->
        intent {
          if (state.isGenerating && message.id == state.generatingMessageId) {
            reduce { state.copy(respondingMessage = null, generatingMessageId = null) }
          }
        }
        message
      }
    }
  }

  @OptIn(OrbitExperimental::class)
  fun updateInputMessage(message: String) {
    blockingIntent {
      reduce { state.copy(inputMessage = message) }
    }
  }

  fun sendTextMessage(message: String) {
    intent {
      val id = UUID.randomUUID().toString()
      reduce { state.copy(inputMessage = "", generatingMessageId = id) }
      chatRepository.sendTextMessage(message = message, id = id)
        .collect { response ->
          reduce { state.copy(respondingMessage = (state.respondingMessage ?: "") + response.text) }
        }
    }
  }

  override fun onCleared() {
    super.onCleared()
    chatRepository.exit()
  }
}
