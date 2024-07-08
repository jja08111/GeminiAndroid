package io.jja08111.gemini.feature.chat.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import dev.jeziellago.compose.markdowntext.MarkdownText

private val HorizontalMargin = 40.dp

@Composable
internal fun ColumnScope.PromptItem(modifier: Modifier = Modifier, text: String) {
  val largeShape = MaterialTheme.shapes.large
  val textColor = MaterialTheme.colorScheme.onSecondaryContainer

  Box(
    modifier = modifier
      .padding(start = HorizontalMargin)
      .clip(
        largeShape.copy(
          topStart = largeShape.topStart,
          topEnd = CornerSize(0.dp),
        ),
      )
      .background(color = MaterialTheme.colorScheme.secondaryContainer)
      .align(alignment = Alignment.End),
  ) {
    Row(
      modifier = Modifier
        .padding(16.dp)
        .align(Alignment.CenterEnd),
    ) {
      MarkdownText(
        modifier = Modifier.align(Alignment.CenterVertically),
        markdown = text,
        style = MaterialTheme.typography.bodyLarge.copy(
          color = textColor,
        ),
      )
    }
  }
}
