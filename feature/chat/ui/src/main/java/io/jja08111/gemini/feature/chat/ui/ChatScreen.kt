package io.jja08111.gemini.feature.chat.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import io.jja08111.gemini.model.Message
import io.jja08111.gemini.model.Role
import io.jja08111.gemini.model.TextContent
import kotlinx.coroutines.launch

@Composable
internal fun ChatScreen(
  uiState: ChatUiState,
  listState: LazyListState = rememberLazyListState(),
  onBackClick: () -> Unit,
  onInputUpdate: (String) -> Unit,
  onSendClick: (String) -> Unit,
) {
  val messages = uiState.messageStream.collectAsLazyPagingItems()
  val coroutineScope = rememberCoroutineScope()

  LaunchedEffect(messages.itemCount) {
    if (messages.itemCount > 0) {
      val lastMessage = messages[0]
      val isMe = lastMessage?.isMe ?: return@LaunchedEffect
      if (isMe) {
        listState.scrollToItem(0)
      }
    }
  }

  Scaffold(
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
      val messageItemModifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 4.dp)

      Box(
        modifier = Modifier
          .fillMaxWidth()
          .weight(1f),
      ) {
        LazyColumn(
          state = listState,
          reverseLayout = true,
          contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        ) {
          if (uiState.generatingMessageId != null) {
            item(key = uiState.generatingMessageId, contentType = Role.Model) {
              uiState.respondingMessage?.let { respondingMessage ->
                TextMessageItem(
                  modifier = messageItemModifier
                    .align(Alignment.CenterStart)
                    .animateContentSize(),
                  text = respondingMessage,
                  isMe = false,
                )
              }
            }
          }
          items(
            count = messages.itemCount,
            key = messages.itemKey { it.id },
            contentType = messages.itemContentType { it.role },
          ) {
            val message = messages[it] ?: return@items
            MessageItem(
              modifier = messageItemModifier
                .align(if (message.isMe) Alignment.CenterEnd else Alignment.CenterStart),
              message = message,
            )
          }
        }
        if (listState.canScrollBackward) {
          GotoBottomButton(
            modifier = Modifier
              .align(Alignment.BottomEnd)
              .padding(16.dp),
            onClick = {
              coroutineScope.launch {
                listState.scrollToItem(0)
              }
            },
          )
        }
      }
      ActionBar(
        inputMessage = uiState.inputMessage,
        enabledSendButton = uiState.canSendMessage,
        onSendClick = onSendClick,
        onInputMessageChange = onInputUpdate,
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
    )
  }
}

private val HorizontalMargin = 40.dp

@Composable
private fun TextMessageItem(modifier: Modifier = Modifier, text: String, isMe: Boolean) {
  val largeShape = MaterialTheme.shapes.large

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
    Text(
      modifier = Modifier
        .padding(16.dp)
        .align(if (isMe) Alignment.CenterEnd else Alignment.CenterStart),
      text = text,
      style = MaterialTheme.typography.bodyLarge.copy(
        color = if (isMe) {
          MaterialTheme.colorScheme.onSecondaryContainer
        } else {
          MaterialTheme.colorScheme.onPrimaryContainer
        },
      ),
    )
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
  enabledSendButton: Boolean,
  onInputMessageChange: (String) -> Unit,
  onSendClick: (String) -> Unit,
) {
  TextField(
    modifier = Modifier
      .fillMaxWidth()
      .padding(bottom = 8.dp, start = 16.dp, end = 16.dp),
    value = inputMessage,
    onValueChange = onInputMessageChange,
    shape = MaterialTheme.shapes.large,
    maxLines = 5,
    colors = TextFieldDefaults.colors(
      focusedIndicatorColor = Color.Transparent,
      unfocusedIndicatorColor = Color.Transparent,
      disabledIndicatorColor = Color.Transparent,
    ),
    trailingIcon = {
      IconButton(
        onClick = { onSendClick(inputMessage) },
        enabled = enabledSendButton,
      ) {
        Icon(
          modifier = Modifier.padding(8.dp),
          imageVector = Icons.AutoMirrored.Filled.Send,
          contentDescription = "Send message",
        )
      }
    },
  )
}
