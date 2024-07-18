package io.jja08111.gemini.feature.chat.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import dev.jeziellago.compose.markdowntext.MarkdownText
import io.jja08111.gemini.model.PromptImage
import kotlin.math.floor

private val CornerSize = CornerSize(2.dp)
private val SingleImageHeightDp = 220.dp

@Composable
internal fun PromptItem(
  modifier: Modifier = Modifier,
  text: String,
  images: List<PromptImage> = emptyList(),
) {
  val largeShape = MaterialTheme.shapes.large
  val textColor = MaterialTheme.colorScheme.onSecondaryContainer
  val imageShape = MaterialTheme.shapes.medium.copy(bottomEnd = CornerSize)

  Column(modifier = modifier.padding(start = 64.dp)) {
    if (images.isNotEmpty()) {
      if (images.size == 1) {
        val image = images.first()
        val imageWidthDp = image.width * (SingleImageHeightDp / image.height)

        Image(
          modifier = Modifier
            // Make size modifier to show placeholder same size as image
            .size(height = SingleImageHeightDp, width = imageWidthDp)
            .align(Alignment.End)
            .clip(imageShape),
          image = image,
        )
      } else {
        ImageGroup(
          modifier = Modifier
            .align(Alignment.End)
            .clip(imageShape),
          images = images,
        )
      }
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

private val ImageSpacing = 4.dp

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ImageGroup(modifier: Modifier = Modifier, images: List<PromptImage>) {
  val maxItemsInEachRow = when (images.size) {
    in 0..1 -> error("Message group should have images more than 2.")
    2, 4 -> 2
    else -> 3
  }
  BoxWithConstraints {
    val maxWith = maxWidth - (2 * ImageSpacing)

    FlowRow(
      modifier = modifier,
      horizontalArrangement = Arrangement.spacedBy(space = ImageSpacing, alignment = Alignment.End),
      verticalArrangement = Arrangement.spacedBy(space = ImageSpacing),
      maxItemsInEachRow = maxItemsInEachRow,
    ) {
      images.forEachIndexed { index, promptImage ->
        val normalImageWidth = Dp(floor(maxWith.value / 3))
        val isCountOdd = images.size % 2 == 1
        val isInLastTwoItem = index >= (images.lastIndex - 1)
        val shouldHaveExpandedWidth =
          maxItemsInEachRow < images.size && isCountOdd && isInLastTwoItem
        val imageWidth = if (shouldHaveExpandedWidth) maxWith / 2 else normalImageWidth

        Image(
          modifier = Modifier
            .size(width = imageWidth, height = normalImageWidth)
            .clip(RoundedCornerShape(CornerSize)),
          image = promptImage,
          contentScale = ContentScale.Crop,
        )
      }
    }
  }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun Image(
  modifier: Modifier = Modifier,
  image: PromptImage,
  contentScale: ContentScale = ContentScale.Fit,
) {
  GlideImage(
    modifier = modifier,
    contentScale = contentScale,
    loading = placeholder(
      ColorPainter(MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.2f)),
    ),
    model = image.path,
    contentDescription = null,
  )
}

@Composable
@Preview
private fun PromptItemPreview() {
  val uri = "https://picsum.photos/200/300"
  val image = PromptImage(width = 200, height = 300, path = uri)

  MaterialTheme {
    PromptItem(
      text = "What is the Gemini?",
      images = listOf(image),
    )
  }
}
