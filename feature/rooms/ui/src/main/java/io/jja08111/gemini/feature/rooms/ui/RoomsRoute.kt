package io.jja08111.gemini.feature.rooms.ui

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun RoomsRoute(viewModel: RoomsViewModel = hiltViewModel(), navigateToChat: (String) -> Unit) {
  val uiState by viewModel.collectAsState()
  val snackbarHostState = remember { SnackbarHostState() }
  val context = LocalContext.current

  viewModel.collectSideEffect {
    when (it) {
      is RoomsSideEffect.Message -> snackbarHostState.showSnackbar(context.getString(it.messageRes))
      is RoomsSideEffect.NavigateToChat -> navigateToChat(it.roomId)
    }
  }

  RoomsScreen(
    uiState = uiState,
    snackbarHostState = snackbarHostState,
    onRoomClick = navigateToChat,
    onCreateClick = viewModel::createRoom,
  )
}
