package io.jja08111.gemini.feature.chat.ui

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import io.github.jja08111.core.navigation.mobile.ChatMobileDestinations
import io.jja08111.gemini.feature.chat.data.model.AttachedImage
import io.jja08111.gemini.feature.chat.ui.fake.FakeChatRepository
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import org.orbitmvi.orbit.test.test
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], manifest = Config.NONE)
class ChatViewModelTest {
  private val defaultSavedStateHandle: SavedStateHandle
    get() = SavedStateHandle().apply {
      this[ChatMobileDestinations.CHAT_ID_ARG] = "chatId"
    }

  @Test
  fun `should attach images when images are not duplicated`() =
    runTest {
      ChatViewModel(
        savedStateHandle = defaultSavedStateHandle,
        chatRepository = FakeChatRepository(),
      ).test(this) {
        // given
        expectInitialState()
        runOnCreate()
        val fakeUri: Uri = Uri.parse("")
        val uris = listOf(fakeUri)

        // when
        containerHost.attachImages(uris)

        // then
        val attachedImages = uris.map { AttachedImage.Uri(it) }
        expectState { copy(attachedImages = attachedImages) }
      }
    }

  @Test
  fun `should filter when attach images with duplicated image`() =
    runTest {
      ChatViewModel(
        savedStateHandle = defaultSavedStateHandle,
        chatRepository = FakeChatRepository(),
      ).test(
        this,
        initialState = ChatUiState(
          messageGroupStream = emptyFlow(),
          attachedImages = listOf(AttachedImage.create(Uri.parse("duplicated"))),
        ),
      ) {
        // given
        expectInitialState()
        runOnCreate()
        val uris = listOf(
          Uri.parse("1"),
          Uri.parse("duplicated"),
        )

        // when
        containerHost.attachImages(uris)

        // then
        expectState {
          copy(
            attachedImages = listOf(
              AttachedImage.create(Uri.parse("duplicated")),
              AttachedImage.create(Uri.parse("1")),
            ),
          )
        }
      }
    }

  @Test
  fun `should remove image successfully`() =
    runTest {
      ChatViewModel(
        savedStateHandle = defaultSavedStateHandle,
        chatRepository = FakeChatRepository(),
      ).test(
        this,
        initialState = ChatUiState(
          messageGroupStream = emptyFlow(),
          attachedImages = listOf(
            AttachedImage.create(Uri.parse("1")),
            AttachedImage.create(Uri.parse("2")),
          ),
        ),
      ) {
        // given
        expectInitialState()
        runOnCreate()

        // when
        containerHost.removeAttachedImage(index = 0)

        // then
        expectState {
          copy(attachedImages = listOf(AttachedImage.create(Uri.parse("2"))))
        }
      }
    }

  @Test
  fun `should clear message and images when send message`() =
    runTest {
      ChatViewModel(
        savedStateHandle = defaultSavedStateHandle,
        chatRepository = FakeChatRepository(),
      ).test(
        this,
        initialState = ChatUiState(
          messageGroupStream = emptyFlow(),
          inputMessage = "input message",
          attachedImages = listOf(
            AttachedImage.create(Uri.parse("1")),
            AttachedImage.create(Uri.parse("2")),
          ),
        ),
      ) {
        // given
        expectInitialState()
        runOnCreate()

        // when
        containerHost.sendMessage()

        // then
        expectState { copy(inputMessage = "", attachedImages = emptyList()) }
        cancelAndIgnoreRemainingItems()
      }
    }
}
