package io.jja08111.gemini.feature.chat.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import io.jja08111.gemini.core.ui.rememberPrevious
import io.jja08111.gemini.model.Message
import io.jja08111.gemini.model.Role
import io.jja08111.gemini.model.TextContent
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
  val messages by uiState.messageStream.collectAsState(initial = emptyList())
  val coroutineScope = rememberCoroutineScope()
  val lastMessageText = (messages.lastOrNull()?.content as? TextContent)?.text
  val keyboardController = LocalSoftwareKeyboardController.current
  val previousCanScrollForward = rememberPrevious(listState.canScrollForward)

  LaunchedEffect(messages.size) {
    if (messages.isNotEmpty()) {
      listState.scrollToBottom()
    }
  }

  LaunchedEffect(lastMessageText?.length) {
    if (previousCanScrollForward == true || lastMessageText == null) {
      return@LaunchedEffect
    }
    val lastItemIndex = listState.layoutInfo.totalItemsCount - 1
    if (listState.firstVisibleItemIndex == lastItemIndex) {
      listState.scrollToBottom()
    } else {
      listState.scrollToLastUserMessage()
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
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .weight(1f),
      ) {
        if (messages.isEmpty()) {
          EmptyContent()
        } else {
          MessageList(
            modifier = Modifier.fillMaxSize(),
            listState = listState,
            messages = messages,
          )
          if (listState.canScrollForward) {
            GotoBottomButton(
              modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
              onClick = {
                coroutineScope.launch {
                  listState.scrollToBottom()
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
private fun BoxScope.MessageList(
  modifier: Modifier = Modifier,
  listState: LazyListState,
  messages: List<Message>,
) {
  LazyColumn(
    modifier = modifier,
    state = listState,
    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
  ) {
    items(
      items = messages,
      key = { it.id },
      contentType = { it.role },
    ) { message ->
      MessageItem(
        modifier = Modifier
          .fillMaxWidth()
          .padding(bottom = 8.dp)
          .align(if (message.isMe) Alignment.CenterEnd else Alignment.CenterStart),
        message = message,
      )
    }
  }
}

private val Message.isMe: Boolean get() = this.role == Role.User

@Composable
private fun MessageItem(modifier: Modifier = Modifier, message: Message) {
  when (val content = message.content) {
    is TextContent -> TextMessageItem(
      modifier = modifier,
      text = content.text,
      isMe = message.isMe,
      isError = message.isError,
    )
  }
}

private val HorizontalMargin = 40.dp

// TODO: Implement retry feature
@Composable
private fun TextMessageItem(
  modifier: Modifier = Modifier,
  text: String,
  isMe: Boolean,
  isError: Boolean = false,
) {
  val largeShape = MaterialTheme.shapes.large
  val textColor = if (isMe) {
    MaterialTheme.colorScheme.onSecondaryContainer
  } else {
    MaterialTheme.colorScheme.onPrimaryContainer
  }

  Box(
    modifier = modifier
      .wrapContentWidth(align = if (isMe) Alignment.End else Alignment.Start)
      .padding(
        start = if (isMe) HorizontalMargin else 0.dp,
        end = if (isMe) 0.dp else HorizontalMargin,
      )
      .clip(
        largeShape.copy(
          topStart = if (isMe) largeShape.topStart else CornerSize(0.dp),
          topEnd = if (isMe) CornerSize(0.dp) else largeShape.topEnd,
        ),
      )
      .background(
        color = if (isMe) {
          MaterialTheme.colorScheme.secondaryContainer
        } else {
          MaterialTheme.colorScheme.primaryContainer
        },
      ),
  ) {
    Row(
      modifier = Modifier
        .padding(16.dp)
        .align(if (isMe) Alignment.CenterEnd else Alignment.CenterStart),
    ) {
      val informationColor = textColor.copy(alpha = 0.4f)
      if (isError) {
        Icon(
          modifier = Modifier
            .align(Alignment.CenterVertically)
            .padding(end = 4.dp),
          imageVector = Icons.Default.Info,
          tint = informationColor,
          contentDescription = null,
        )
      }
      Text(
        modifier = Modifier.align(Alignment.CenterVertically),
        text = buildAnnotatedString {
          when {
            isError || text.isBlank() -> {
              pushStyle(SpanStyle(color = informationColor))
              append(
                stringResource(
                  id = if (isError) {
                    io.jja08111.gemini.core.ui.R.string.something_went_wrong
                  } else {
                    io.jja08111.gemini.core.ui.R.string.empty_content
                  },
                ),
              )
              pop()
            }

            else -> append(text)
          }
        },
        style = MaterialTheme.typography.bodyLarge.copy(
          color = textColor,
        ),
      )
    }
  }
}

@Composable
private fun GotoBottomButton(modifier: Modifier = Modifier, onClick: () -> Unit) {
  SmallFloatingActionButton(
    modifier = modifier,
    containerColor = MaterialTheme.colorScheme.background,
    onClick = onClick,
  ) {
    Icon(
      imageVector = Icons.Default.KeyboardArrowDown,
      contentDescription = "Go to bottom",
      tint = MaterialTheme.colorScheme.primary,
    )
  }
}

@Composable
private fun ActionBar(
  inputMessage: String,
  canSend: Boolean,
  onInputMessageChange: (String) -> Unit,
  onSendClick: (String) -> Unit,
) {
  TextField(
    modifier = Modifier
      .fillMaxWidth()
      .background(color = MaterialTheme.colorScheme.background)
      .padding(bottom = 8.dp, start = 16.dp, end = 16.dp),
    value = inputMessage,
    onValueChange = onInputMessageChange,
    shape = MaterialTheme.shapes.extraLarge,
    maxLines = 5,
    colors = TextFieldDefaults.colors(
      focusedIndicatorColor = Color.Transparent,
      unfocusedIndicatorColor = Color.Transparent,
      disabledIndicatorColor = Color.Transparent,
    ),
    placeholder = {
      Text(text = stringResource(R.string.feature_chat_ui_message_gemini_placeholder))
    },
    trailingIcon = {
      if (canSend) {
        IconButton(
          onClick = { onSendClick(inputMessage) },
        ) {
          Icon(
            modifier = Modifier.padding(8.dp),
            imageVector = Icons.AutoMirrored.Filled.Send,
            contentDescription = "Send message",
          )
        }
      }
    },
  )
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

private suspend fun LazyListState.scrollToLastUserMessage() {
  val itemCount = layoutInfo.totalItemsCount
  if (itemCount > 1) {
    scrollToItem(itemCount - 2)
  }
}

private suspend fun LazyListState.scrollToBottom() {
  val itemCount = layoutInfo.totalItemsCount
  if (itemCount > 1) {
    scrollToItem(itemCount - 1, scrollOffset = Int.MAX_VALUE)
  }
}
