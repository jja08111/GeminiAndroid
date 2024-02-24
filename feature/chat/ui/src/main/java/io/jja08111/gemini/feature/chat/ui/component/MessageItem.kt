package io.jja08111.gemini.feature.chat.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.jja08111.gemini.core.ui.R
import io.jja08111.gemini.model.Message
import io.jja08111.gemini.model.MessageState
import io.jja08111.gemini.model.TextContent

@Composable
internal fun MessageItem(modifier: Modifier = Modifier, message: Message) {
  when (val content = message.content) {
    is TextContent -> TextMessageItem(
      modifier = modifier,
      text = content.text,
      isMe = message.isFromUser,
      state = message.state,
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
  state: MessageState,
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
      when {
        state == MessageState.Error -> {
          ErrorMessageItem(textColor = textColor.copy(alpha = 0.4f))
        }

        state == MessageState.Generating && text.isEmpty() -> {
          Shimmer()
        }

        else -> {
          Text(
            modifier = Modifier.align(Alignment.CenterVertically),
            text = text,
            style = MaterialTheme.typography.bodyLarge.copy(
              color = textColor,
            ),
          )
        }
      }
    }
  }
}

@Composable
private fun ErrorMessageItem(textColor: Color) {
  Row {
    Icon(
      modifier = Modifier
        .align(Alignment.CenterVertically)
        .padding(end = 4.dp),
      imageVector = Icons.Default.Info,
      tint = MaterialTheme.colorScheme.error,
      contentDescription = null,
    )
    Text(
      text = stringResource(id = R.string.something_went_wrong),
      style = MaterialTheme.typography.bodyLarge.copy(
        color = textColor,
      ),
    )
  }
}

@Composable
private fun Shimmer() {
  val fontScale = LocalDensity.current.fontScale
  val color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)

  BoxWithConstraints ConstraintsScope@{
    Column {
      Box(
        modifier = Modifier
          .height(20.dp * fontScale)
          .fillMaxWidth()
          .clip(RoundedCornerShape(4.dp))
          .background(color = color),
      )
      Spacer(modifier = Modifier.height(8.dp))
      Box(
        modifier = Modifier
          .height(20.dp * fontScale)
          .fillMaxWidth()
          .clip(RoundedCornerShape(4.dp))
          .background(color = color),
      )
      Spacer(modifier = Modifier.height(8.dp))
      Box(
        modifier = Modifier
          .height(20.dp * fontScale)
          .width(this@ConstraintsScope.maxWidth - 120.dp)
          .clip(RoundedCornerShape(4.dp))
          .background(color = color),
      )
    }
  }
}

@Preview(showBackground = true)
@Composable
private fun ErrorMessagePreview() {
  ErrorMessageItem(textColor = Color.Black)
}

@Preview(showBackground = true)
@Composable
private fun ShimmerPreview() {
  Shimmer()
}
