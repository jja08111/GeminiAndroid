package io.jja08111.gemini.feature.chat.ui

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
fun ChatRoute(viewModel: ChatViewModel = hiltViewModel(), onBackClick: () -> Unit) {
  val uiState by viewModel.collectAsState()
  val listState = rememberLazyListState()
  val context = LocalContext.current
  val snackbarHostState = remember { SnackbarHostState() }

  viewModel.collectSideEffect {
    when (it) {
      is ChatSideEffect.UserMessage -> snackbarHostState.showSnackbar(it.message.asString(context))
    }
  }

  ChatScreen(
    uiState = uiState,
    snackbarHostState = snackbarHostState,
    listState = listState,
    onBackClick = onBackClick,
    onInputUpdate = viewModel::updateInputMessage,
    onSendClick = viewModel::sendTextMessage,
    onRegenerateOnErrorClick = viewModel::regenerateOnError,
    onRegenerateResponseClick = viewModel::regenerateResponse,
  )
}
