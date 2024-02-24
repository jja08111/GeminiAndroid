package io.jja08111.gemini.feature.chat.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import io.jja08111.gemini.core.ui.R
import io.jja08111.gemini.model.Message
import io.jja08111.gemini.model.TextContent

@Composable
internal fun MessageItem(modifier: Modifier = Modifier, message: Message) {
  when (val content = message.content) {
    is TextContent -> TextMessageItem(
      modifier = modifier,
      text = content.text,
      isMe = message.isFromUser,
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
  isError: Boolean,
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
                    R.string.something_went_wrong
                  } else {
                    R.string.empty_content
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
