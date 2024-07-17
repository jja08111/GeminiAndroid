package io.jja08111.gemini.feature.chat.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun ChatRoute(
  viewModel: ChatViewModel = hiltViewModel(),
  popBackStack: () -> Unit,
  navigateToSelectResponse: (promptId: String) -> Unit,
) {
  val uiState by viewModel.collectAsState()
  val listState = rememberLazyListState()
  val context = LocalContext.current
  val snackbarHostState = remember { SnackbarHostState() }

  val launcher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.GetContent(),
  ) { uri: Uri? ->
    if (uri != null) {
      viewModel.attachImage(uri)
    }
  }

  viewModel.collectSideEffect {
    when (it) {
      is ChatSideEffect.UserMessage -> snackbarHostState.showSnackbar(it.message.asString(context))
    }
  }

  ChatScreen(
    uiState = uiState,
    snackbarHostState = snackbarHostState,
    listState = listState,
    onBackClick = popBackStack,
    onInputUpdate = viewModel::updateInputMessage,
    onAlbumClick = { launcher.launch("image/*") },
    onSendClick = viewModel::sendMessage,
    onRegenerateOnErrorClick = viewModel::regenerateOnError,
    onSelectResponseClick = navigateToSelectResponse,
    onRegenerateResponseClick = viewModel::regenerateResponse,
    onRemoveImageClick = viewModel::removeAttachedImage,
  )
}
