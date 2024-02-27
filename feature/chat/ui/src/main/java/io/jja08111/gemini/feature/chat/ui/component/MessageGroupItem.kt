package io.jja08111.gemini.feature.chat.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import io.jja08111.gemini.model.MessageGroup

@Composable
internal fun MessageGroupItem(
  modifier: Modifier = Modifier,
  messageGroup: MessageGroup,
  generatingMessage: String?,
) {
  val modelResponse = messageGroup.selectedResponse

  Column(
    modifier = modifier,
  ) {
    Spacer(modifier = Modifier.padding(2.dp))
    TextMessageItem(
      text = messageGroup.prompt.text,
      isMe = true,
    )
    Spacer(modifier = Modifier.padding(4.dp))
    TextMessageItem(
      text = if (modelResponse.isGenerating && generatingMessage != null) {
        generatingMessage
      } else {
        modelResponse.text
      },
      isMe = false,
      isLoading = modelResponse.isGenerating,
      isError = modelResponse.isError,
    )
    Spacer(modifier = Modifier.padding(2.dp))
  }
}

private val HorizontalMargin = 40.dp

// TODO: Implement retry feature
@Composable
private fun ColumnScope.TextMessageItem(
  modifier: Modifier = Modifier,
  text: String,
  isMe: Boolean,
  isLoading: Boolean = false,
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
      )
      .align(alignment = if (isMe) Alignment.End else Alignment.Start),
  ) {
    Row(
      modifier = Modifier
        .padding(16.dp)
        .align(if (isMe) Alignment.CenterEnd else Alignment.CenterStart),
    ) {
      when {
        isError -> ErrorMessageItem(textColor = textColor.copy(alpha = 0.4f))
        isLoading && text.isEmpty() -> Shimmer()
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
