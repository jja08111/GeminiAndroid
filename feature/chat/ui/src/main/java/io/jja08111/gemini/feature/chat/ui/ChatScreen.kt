package io.jja08111.gemini.feature.chat.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.jja08111.gemini.core.ui.rememberPrevious
import io.jja08111.gemini.feature.chat.ui.component.GeminiTextField
import io.jja08111.gemini.feature.chat.ui.component.GotoBottomButton
import io.jja08111.gemini.feature.chat.ui.component.MessageGroup
import io.jja08111.gemini.feature.chat.ui.component.ModelResponseDropdownMenu
import io.jja08111.gemini.feature.chat.ui.component.TrailingButtonState
import io.jja08111.gemini.feature.chat.ui.component.rememberModelResponseDropdownMenuState
import io.jja08111.gemini.model.MessageGroup
import io.jja08111.gemini.model.ModelResponse
import io.jja08111.gemini.model.ModelResponseState
import kotlinx.coroutines.launch

// TODO: Show keyboard when enter this screen
@Composable
internal fun ChatScreen(
  uiState: ChatUiState,
  snackbarHostState: SnackbarHostState,
  listState: LazyListState = rememberLazyListState(),
  onBackClick: () -> Unit,
  onInputUpdate: (prompt: String) -> Unit,
  onCameraClick: () -> Unit,
  onAlbumClick: () -> Unit,
  onRemoveImageClick: (index: Int) -> Unit,
  onSendClick: () -> Unit,
  onRegenerateOnErrorClick: () -> Unit,
  onSelectResponseClick: (promptId: String) -> Unit,
  // TODO: Change String type to value class
  onRegenerateResponseClick: (responseId: String) -> Unit,
) {
  val messageGroups by uiState.messageGroupStream.collectAsStateWithLifecycle(emptyList())
  val isGenerating =
    messageGroups.lastOrNull()?.selectedResponse?.state == ModelResponseState.Generating
  val lastMessageGroup = messageGroups.lastOrNull()
  val hasLastResponseError = lastMessageGroup?.selectedResponse?.state == ModelResponseState.Error
  val canSendMessage = !isGenerating && uiState.inputMessage.isNotEmpty() && !hasLastResponseError
  val coroutineScope = rememberCoroutineScope()
  val keyboardController = LocalSoftwareKeyboardController.current
  val showGotoBottomButton by remember {
    derivedStateOf {
      listState.firstVisibleItemIndex != listState.layoutInfo.totalItemsCount - 1
    }
  }
  val modelResponseDropdownMenuState = rememberModelResponseDropdownMenuState()
  var leadingExpanded by rememberSaveable { mutableStateOf(true) }

  ScrollPositionSideEffect(messageGroups = messageGroups, listState = listState)

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background),
  ) {
    Scaffold(
      modifier = Modifier.weight(1f),
      snackbarHost = { SnackbarHost(snackbarHostState) },
      topBar = {
        TopAppBar(
          title = {},
          navigationIcon = {
            IconButton(onClick = onBackClick) {
              Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back button",
              )
            }
          },
        )
      },
    ) { innerPadding ->
      Column(
        modifier = Modifier
          .padding(innerPadding)
          .pointerInput(Unit, modelResponseDropdownMenuState::awaitPressEvent),
      ) {
        BoxWithConstraints(
          modifier = Modifier
            .fillMaxWidth()
            .weight(1f),
        ) {
          if (messageGroups.isEmpty()) {
            EmptyContent()
          } else {
            MessageGroupList(
              modifier = Modifier.fillMaxSize(),
              listState = listState,
              messageGroups = messageGroups,
              onModelResponseLongClick = modelResponseDropdownMenuState::expand,
            )
            if (showGotoBottomButton) {
              GotoBottomButton(
                modifier = Modifier
                  .align(Alignment.BottomEnd)
                  .padding(16.dp),
                onClick = {
                  coroutineScope.launch {
                    listState.scrollToLastMessageGroup(animated = true)
                  }
                },
              )
            }
          }
          VerticalGradient()
          ModelResponseDropdownMenu(
            state = modelResponseDropdownMenuState,
            onDismissRequest = modelResponseDropdownMenuState::hide,
            onSelectResponseClick = {
              val selected = modelResponseDropdownMenuState.selectedModelResponse
              val messageGroup = messageGroups.firstOrNull {
                it.selectedResponse.id == selected?.id
              } ?: error("Cannot find message group by selected response ID.")
              onSelectResponseClick(messageGroup.prompt.id)
              modelResponseDropdownMenuState.hide()
            },
            onRegenerateClick = {
              val selected = modelResponseDropdownMenuState.selectedModelResponse
              checkNotNull(selected)
              onRegenerateResponseClick(selected.id)
              modelResponseDropdownMenuState.hide()
            },
          )
        }
      }
    }

    if (uiState.failedToJoinRoom) {
      BottomButton(
        modifier = Modifier
          .align(Alignment.CenterHorizontally)
          .padding(vertical = 8.dp),
        label = "Retry to Join",
        imageVector = Icons.Default.Refresh,
        contentDescription = "Retry to join room",
        onClick = { /* TODO: Implement here */ },
      )
    } else if (hasLastResponseError) {
      BottomButton(
        modifier = Modifier
          .align(Alignment.CenterHorizontally)
          .padding(vertical = 8.dp),
        label = "Regenerate",
        imageVector = Icons.Default.Refresh,
        contentDescription = "Regenerate response",
        onClick = onRegenerateOnErrorClick,
      )
    } else {
      GeminiTextField(
        modifier = Modifier
          .fillMaxWidth()
          .padding(bottom = 8.dp, start = 16.dp, end = 16.dp),
        text = uiState.inputMessage,
        images = uiState.attachedImages,
        leadingExpanded = leadingExpanded,
        trailingButtonState = if (isGenerating) {
          TrailingButtonState.Stop
        } else if (canSendMessage) {
          TrailingButtonState.Send
        } else {
          TrailingButtonState.Empty
        },
        onSendClick = {
          onSendClick()
          keyboardController?.hide()
        },
        onTextChange = onInputUpdate,
        onCameraClick = onCameraClick,
        onAlbumClick = onAlbumClick,
        onExpandChange = { leadingExpanded = it },
        onRemoveImageClick = onRemoveImageClick,
      )
    }
  }
}

@Composable
private fun BoxScope.EmptyContent() {
  Text(
    modifier = Modifier.align(Alignment.Center),
    text = stringResource(R.string.feature_chat_ui_empty_content_title),
    style = MaterialTheme.typography.headlineMedium.copy(
      color = MaterialTheme.colorScheme.onBackground,
    ),
  )
}

@Composable
private fun BoxWithConstraintsScope.MessageGroupList(
  modifier: Modifier = Modifier,
  listState: LazyListState,
  messageGroups: List<MessageGroup>,
  onModelResponseLongClick: (ModelResponse) -> Unit,
) {
  LazyColumn(
    modifier = modifier,
    state = listState,
  ) {
    items(
      items = messageGroups,
      key = { it.prompt.id },
    ) { messageGroup ->
      val isLast = messageGroup == messageGroups.lastOrNull()
      MessageGroup(
        modifier = Modifier
          .fillMaxWidth()
          .heightIn(min = if (isLast) maxHeight else 0.dp)
          .padding(bottom = if (isLast) 16.dp else 0.dp),
        messageGroup = messageGroup,
        onModelResponseLongClick = onModelResponseLongClick,
      )
    }
  }
}

@Composable
private fun BoxScope.VerticalGradient() {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .align(Alignment.BottomCenter)
      .height(24.dp)
      .background(
        brush = Brush.verticalGradient(
          colors = listOf(
            Color.Transparent,
            MaterialTheme.colorScheme.background,
          ),
        ),
      ),
  )
}

@Composable
private fun BottomButton(
  modifier: Modifier = Modifier,
  label: String,
  imageVector: ImageVector,
  contentDescription: String,
  onClick: () -> Unit,
) {
  val contentColor = MaterialTheme.colorScheme.onPrimary

  Button(modifier = modifier, onClick = onClick) {
    Icon(
      modifier = Modifier.size(20.dp),
      imageVector = imageVector,
      tint = contentColor,
      contentDescription = contentDescription,
    )
    Spacer(modifier = Modifier.width(8.dp))
    Text(text = label, style = MaterialTheme.typography.titleSmall, color = contentColor)
  }
}

private suspend fun LazyListState.scrollToLastMessageGroup(animated: Boolean = false) {
  val itemCount = layoutInfo.totalItemsCount
  if (itemCount > 0) {
    val targetIndex = itemCount - 1
    if (animated) {
      animateScrollToItem(targetIndex)
    } else {
      scrollToItem(targetIndex)
    }
  }
}

@Composable
private fun ScrollPositionSideEffect(messageGroups: List<MessageGroup>, listState: LazyListState) {
  val messageGroupSize = messageGroups.size
  val lastResponseTextLength = messageGroups.lastOrNull()?.selectedResponse?.text?.length ?: 0
  val isMessageGroupNotEmpty = messageGroupSize > 0
  val previousMessageGroupSize = rememberPrevious(current = messageGroupSize)
  var isDoneInitScrolling by rememberSaveable { mutableStateOf(false) }

  // If user send message, scroll to last message group
  LaunchedEffect(messageGroupSize) {
    if (previousMessageGroupSize != messageGroupSize && lastResponseTextLength == 0) {
      listState.scrollToLastMessageGroup(animated = true)
    }
  }

  // If Screen is initialized, scroll to last message group
  LaunchedEffect(isMessageGroupNotEmpty) {
    if (isMessageGroupNotEmpty && !isDoneInitScrolling) {
      listState.scrollToLastMessageGroup(animated = false)
      isDoneInitScrolling = true
    }
  }
}
