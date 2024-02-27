package io.jja08111.gemini.feature.chat.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.jja08111.gemini.core.ui.rememberPrevious
import io.jja08111.gemini.feature.chat.ui.component.ActionBar
import io.jja08111.gemini.feature.chat.ui.component.GotoBottomButton
import io.jja08111.gemini.feature.chat.ui.component.MessageGroupItem
import io.jja08111.gemini.model.MessageGroup
import kotlinx.coroutines.launch

// TODO: Show keyboard when enter this screen
@Composable
internal fun ChatScreen(
  uiState: ChatUiState,
  snackbarHostState: SnackbarHostState,
  listState: LazyListState = rememberLazyListState(),
  onBackClick: () -> Unit,
  onInputUpdate: (String) -> Unit,
  onSendClick: (String) -> Unit,
) {
  val messageGroups by uiState.messageGroupStream.collectAsState(initial = emptyList())
  val isGenerating = uiState.isGenerating
  val previousCanScrollForward = rememberPrevious(listState.canScrollForward)
  val coroutineScope = rememberCoroutineScope()
  val keyboardController = LocalSoftwareKeyboardController.current
  var fetched by remember { mutableStateOf(false) }
  val showGotoBottomButton by remember {
    derivedStateOf {
      listState.firstVisibleItemIndex != listState.layoutInfo.totalItemsCount - 1 ||
        listState.firstVisibleItemScrollOffset > 80
    }
  }
  val generatingMessage = uiState.generatingMessage

  // TODO: Fix here...
  LaunchedEffect(generatingMessage?.length) {
    if (previousCanScrollForward == true || generatingMessage == null) {
      return@LaunchedEffect
    }
    val lastItemIndex = listState.layoutInfo.totalItemsCount - 1
    if (listState.firstVisibleItemIndex == lastItemIndex) {
      listState.scrollToBottom(animated = false)
    } else {
      listState.scrollToLastMessageGroup(animated = true)
    }
  }

  LaunchedEffect(messageGroups.size) {
    if (messageGroups.isNotEmpty()) {
      listState.scrollToLastMessageGroup(animated = fetched)
      fetched = true
    }
  }

  Scaffold(
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
    Column(modifier = Modifier.padding(innerPadding)) {
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
            generatingMessage = generatingMessage,
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
      }
      ActionBar(
        inputMessage = uiState.inputMessage,
        canSend = uiState.canSendMessage,
        showLoading = isGenerating,
        onSendClick = {
          onSendClick(it)
          keyboardController?.hide()
        },
        onInputMessageChange = onInputUpdate,
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
  generatingMessage: String?,
) {
  LazyColumn(
    modifier = modifier,
    state = listState,
    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 2.dp),
  ) {
    items(
      items = messageGroups,
      key = { it.prompt.id },
    ) { messageGroup ->
      val isLast = messageGroup == messageGroups.lastOrNull()
      val isGenerating = messageGroup.selectedResponse.isGenerating

      MessageGroupItem(
        modifier = Modifier
          .fillMaxWidth()
          .heightIn(min = if (isLast) maxHeight else 0.dp),
        messageGroup = messageGroup,
        generatingMessage = if (isLast && isGenerating) generatingMessage else null,
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

private suspend fun LazyListState.scrollToBottom(animated: Boolean = false) {
  val itemCount = layoutInfo.totalItemsCount
  if (itemCount > 0) {
    val targetIndex = itemCount - 1
    if (animated) {
      animateScrollToItem(targetIndex, scrollOffset = Int.MAX_VALUE)
    } else {
      scrollToItem(targetIndex, scrollOffset = Int.MAX_VALUE)
    }
  }
}
