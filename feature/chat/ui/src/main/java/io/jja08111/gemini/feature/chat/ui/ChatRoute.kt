package io.jja08111.gemini.feature.chat.ui

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import org.orbitmvi.orbit.compose.collectAsState

@Composable
fun ChatRoute(viewModel: ChatViewModel = hiltViewModel(), onBackClick: () -> Unit) {
  val uiState = viewModel.collectAsState().value
  val listState = rememberLazyListState()

  ChatScreen(
    uiState = uiState,
    listState = listState,
    onBackClick = onBackClick,
    onInputUpdate = viewModel::updateInputMessage,
    onSendClick = viewModel::sendTextMessage,
  )
}
