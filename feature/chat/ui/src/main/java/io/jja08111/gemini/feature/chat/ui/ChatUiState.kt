package io.jja08111.gemini.feature.chat.ui

import android.net.Uri
import io.jja08111.gemini.model.MessageGroup
import kotlinx.coroutines.flow.Flow

data class ChatUiState(
  val messageGroupStream: Flow<List<MessageGroup>>,
  val inputMessage: String = "",
  val attachedImageUris: List<Uri> = emptyList(),
) {
  val remainingImageCount: Int
    get() = MAX_IMAGE_COUNT - attachedImageUris.size
}
