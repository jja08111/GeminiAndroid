package io.jja08111.gemini.feature.chat.ui

import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
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

  val imagePickerLauncher = rememberImagePickerLauncher(
    maxItems = uiState.remainingImageCount,
    onPickImages = viewModel::attachImages,
  )

  val takePictureLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.TakePicturePreview(),
    onResult = { bitmap -> bitmap?.let { viewModel.attachImage(it) } },
  )

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
    onCameraClick = takePictureLauncher::launch,
    onAlbumClick = {
      imagePickerLauncher.launch(
        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly),
      )
    },
    onSendClick = viewModel::sendMessage,
    onRegenerateOnErrorClick = viewModel::regenerateOnError,
    onSelectResponseClick = navigateToSelectResponse,
    onRegenerateResponseClick = viewModel::regenerateResponse,
    onRemoveImageClick = viewModel::removeAttachedImage,
  )
}

@Composable
private fun rememberImagePickerLauncher(
  maxItems: Int,
  onPickImages: (uris: List<Uri>) -> Unit,
): ManagedActivityResultLauncher<PickVisualMediaRequest, out Any?> {
  return if (maxItems > 1) {
    rememberLauncherForActivityResult(
      contract = ActivityResultContracts.PickMultipleVisualMedia(
        maxItems = maxItems,
      ),
      onResult = onPickImages,
    )
  } else {
    rememberLauncherForActivityResult(
      contract = ActivityResultContracts.PickVisualMedia(),
      onResult = { uri -> uri?.let { onPickImages(listOf(it)) } },
    )
  }
}
