package io.jja08111.gemini.feature.chat.ui.select.response

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun SelectResponseRoute(
  viewModel: SelectResponseViewModel = hiltViewModel(),
  popBackStack: () -> Unit,
) {
  val uiState by viewModel.collectAsState()
  val context = LocalContext.current
  val snackbarHostState = remember { SnackbarHostState() }

  viewModel.collectSideEffect {
    when (it) {
      SelectResponseSideEffect.PopBackStack -> popBackStack()
      is SelectResponseSideEffect.UserMessage -> snackbarHostState.showSnackbar(
        it.message.asString(context),
      )
    }
  }

  SelectResponseScreen(
    uiState = uiState,
    snackbarHostState = snackbarHostState,
    onBackClick = popBackStack,
    onResponseClick = viewModel::changeSelectedResponse,
  )
}
