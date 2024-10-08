package io.jja08111.gemini.feature.chat.ui.component

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.jeziellago.compose.markdowntext.MarkdownText
import io.jja08111.gemini.core.ui.R

private fun emptyCallback() {
  // Nothing
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun ExpandableModelResponseItem(
  modifier: Modifier = Modifier,
  title: String? = null,
  text: String,
  expanded: Boolean,
  isLoading: Boolean = false,
  isError: Boolean = false,
  onExpandClick: (Boolean) -> Unit,
  onClick: () -> Unit = ::emptyCallback,
  onLongClick: (() -> Unit)? = null,
) {
  Card(
    modifier = modifier
      .animateContentSize()
      .combinedClickable(onClick = onClick, onLongClick = onLongClick),
  ) {
    Box {
      ModelResponseItem(
        modifier = Modifier.padding(top = 16.dp, bottom = 16.dp, start = 16.dp, end = 48.dp),
        title = title,
        text = text,
        maxLines = if (expanded) Int.MAX_VALUE else 3,
        isLoading = isLoading,
        isError = isError,
        onClick = onClick,
        onLongClick = onLongClick,
      )
      IconButton(
        modifier = Modifier.align(Alignment.BottomEnd),
        onClick = { onExpandClick(!expanded) },
      ) {
        Icon(
          imageVector = if (expanded) {
            Icons.Default.KeyboardArrowUp
          } else {
            Icons.Default.KeyboardArrowDown
          },
          contentDescription = if (expanded) "Collapse response" else "Expand response",
        )
      }
    }
  }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun ModelResponseItem(
  modifier: Modifier = Modifier,
  title: String? = null,
  text: String,
  maxLines: Int = Int.MAX_VALUE,
  isLoading: Boolean = false,
  isError: Boolean = false,
  onClick: () -> Unit = ::emptyCallback,
  onLongClick: (() -> Unit)? = null,
) {
  val textColor = LocalContentColor.current

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .combinedClickable(onClick = onClick, onLongClick = onLongClick)
      .then(modifier),
  ) {
    Row(modifier = Modifier.align(Alignment.Start)) {
      Icon(
        modifier = Modifier
          .padding(end = 8.dp)
          .size(24.dp),
        imageVector = Icons.Default.Star,
        tint = MaterialTheme.colorScheme.primary,
        contentDescription = "Gemini avatar",
      )
      Text(
        text = title ?: "Gemini",
        style = MaterialTheme.typography.titleMedium.copy(
          fontWeight = FontWeight.Bold,
          color = textColor,
        ),
      )
    }
    Spacer(modifier = Modifier.height(12.dp))
    when {
      isError -> ErrorMessageItem(textColor = textColor.copy(alpha = 0.4f))
      isLoading && text.isEmpty() -> Shimmer()
      else -> {
        MarkdownText(
          modifier = Modifier.align(Alignment.Start),
          disableLinkMovementMethod = true,
          maxLines = maxLines,
          markdown = text,
          truncateOnTextOverflow = true,
          style = MaterialTheme.typography.bodyLarge.copy(
            color = textColor,
          ),
        )
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
