package io.jja08111.gemini.feature.chat.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import dev.jeziellago.compose.markdowntext.MarkdownText
import io.jja08111.gemini.model.PromptImage

internal val PromptItemHorizontalMargin = 40.dp
private val CornerSize = CornerSize(2.dp)

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
internal fun PromptItem(
  modifier: Modifier = Modifier,
  text: String,
  images: List<PromptImage> = emptyList(),
) {
  val largeShape = MaterialTheme.shapes.large
  val textColor = MaterialTheme.colorScheme.onSecondaryContainer

  Column(modifier = modifier) {
    if (images.isNotEmpty()) {
      // TODO: Implement multiple image view
      val image = images.first()
      GlideImage(
        modifier = Modifier
          .heightIn(min = 100.dp, max = 180.dp)
          .clip(MaterialTheme.shapes.medium.copy(bottomEnd = CornerSize))
          .align(Alignment.End),
        contentScale = ContentScale.Fit,
        loading = placeholder(
          ColorPainter(MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.2f)),
        ),
        model = image.path,
        contentDescription = null,
      )
      Spacer(modifier = Modifier.height(4.dp))
    }
    Box(
      modifier = Modifier
        .clip(largeShape.copy(topEnd = CornerSize))
        .background(color = MaterialTheme.colorScheme.secondaryContainer)
        .align(Alignment.End),
    ) {
      Row(
        modifier = Modifier
          .padding(16.dp)
          .align(Alignment.CenterEnd),
      ) {
        MarkdownText(
          modifier = Modifier.align(Alignment.CenterVertically),
          markdown = text,
          style = MaterialTheme.typography.bodyLarge.copy(color = textColor),
        )
      }
    }
  }
}

@Composable
@Preview
private fun PromptItemPreview() {
  MaterialTheme {
    PromptItem(
      text = "What is the Gemini?",
    )
  }
}
