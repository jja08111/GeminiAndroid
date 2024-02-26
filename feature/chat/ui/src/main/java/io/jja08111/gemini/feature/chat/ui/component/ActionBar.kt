package io.jja08111.gemini.feature.chat.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.jja08111.gemini.feature.chat.ui.R

@Composable
internal fun ActionBar(
  inputMessage: String,
  canSend: Boolean,
  showLoading: Boolean,
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
      Row {
        if (showLoading) {
          CircularProgressIndicator()
        }
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
      }
    },
  )
}
