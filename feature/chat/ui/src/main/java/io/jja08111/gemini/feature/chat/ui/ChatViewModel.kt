package io.jja08111.gemini.feature.chat.ui

import android.net.Uri
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.type.ResponseStoppedException
import com.google.ai.client.generativeai.type.ServerException
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jja08111.core.navigation.mobile.ChatMobileDestinations
import io.jja08111.gemini.core.ui.Message
import io.jja08111.gemini.feature.chat.data.repository.ChatRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.annotation.OrbitExperimental
import org.orbitmvi.orbit.syntax.simple.blockingIntent
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

val MAX_IMAGE_COUNT = 3

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
      messageGroupStream = chatRepository.join(roomId).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList(),
      ),
    ),
  )

  @OptIn(OrbitExperimental::class)
  fun updateInputMessage(message: String) {
    blockingIntent {
      reduce { state.copy(inputMessage = message) }
    }
  }

  fun attachImage(imageUri: Uri) {
    intent {
      if (state.attachedImageUris.contains(imageUri)) {
        return@intent
      }
      reduce { state.copy(attachedImageUris = state.attachedImageUris + imageUri) }
    }
  }

  fun removeAttachedImage(uri: Uri) {
    intent {
      reduce {
        state.copy(
          attachedImageUris = state.attachedImageUris.filterNot { it == uri },
        )
      }
    }
  }

  fun sendMessage(message: String, imageUris: List<Uri>) {
    intent {
      reduce { state.copy(inputMessage = "", attachedImageUris = emptyList()) }
    }
    viewModelScope.launch {
      chatRepository.sendMessage(
        message = message,
        imageUris = imageUris,
        onRoomCreated = { stream ->
          intent { reduce { state.copy(messageGroupStream = stream) } }
        },
      ).onFailure(::handleChatException)
    }
  }

  fun regenerateOnError() {
    intent {
      chatRepository.regenerateOnError()
        .onFailure(::handleChatException)
    }
  }

  fun regenerateResponse(responseId: String) {
    intent {
      chatRepository.regenerateResponse(responseId)
        .onFailure(::handleChatException)
    }
  }

  private fun handleChatException(throwable: Throwable) {
    Log.e(TAG, "Error caused when generating response. $throwable")
    intent {
      when (throwable) {
        is ResponseStoppedException -> postSideEffect(
          ChatSideEffect.UserMessage(
            // TODO: 에러 메시지 finishReason으로 디테일하게 바꾸기
            message = Message.Resource(
              R.string.feature_chat_ui_response_stopped_message,
              throwable.response.candidates.first().finishReason?.name ?: "",
            ),
          ),
        )

        is ServerException -> postSideEffect(
          ChatSideEffect.UserMessage(
            Message.Resource(R.string.feature_chat_ui_server_error_message),
          ),
        )

        else -> postSideEffect(
          ChatSideEffect.UserMessage(
            Message.Resource(R.string.feature_chat_ui_unknown_error_message),
          ),
        )
      }
    }
  }

  override fun onCleared() {
    super.onCleared()
    chatRepository.exit()
  }

  companion object {
    private const val TAG = "ChatViewModel"
  }
}
