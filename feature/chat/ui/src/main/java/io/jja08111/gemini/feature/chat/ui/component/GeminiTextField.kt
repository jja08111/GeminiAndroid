package io.jja08111.gemini.feature.chat.ui.component

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import io.jja08111.gemini.core.ui.CrossFade
import io.jja08111.gemini.feature.chat.ui.R

internal enum class TrailingButtonState {
  Empty,
  Send,
  Stop,
}

@Composable
internal fun GeminiTextField(
  modifier: Modifier = Modifier,
  text: String,
  onTextChange: (String) -> Unit,
  imageUri: Uri?,
  trailingButtonState: TrailingButtonState,
  leadingExpanded: Boolean = true,
  onExpandChange: (Boolean) -> Unit,
  onSendClick: (String) -> Unit,
  onCameraClick: () -> Unit,
  onAlbumClick: () -> Unit,
  onRemoveImageClick: () -> Unit,
) {
  val interactionSource = remember { MutableInteractionSource() }
  val labelColor = MaterialTheme.colorScheme.onBackground
  val labelTextStyle = MaterialTheme.typography.bodyLarge.copy(color = labelColor)
  val containerColor = MaterialTheme.colorScheme.secondaryContainer

  LaunchedEffect(Unit) {
    interactionSource.interactions.collect { interaction ->
      if (interaction is PressInteraction.Release) {
        onExpandChange(false)
      }
    }
  }

  BasicTextField(
    modifier = modifier,
    value = text,
    onValueChange = {
      onTextChange(it)
      onExpandChange(false)
    },
    interactionSource = interactionSource,
    maxLines = 5,
    textStyle = labelTextStyle,
  ) { innerTextField ->
    Row(
      modifier = Modifier
        .clip(MaterialTheme.shapes.extraLarge)
        .background(color = containerColor),
    ) {
      if (imageUri == null) {
        LeadingButton(
          modifier = Modifier.align(Alignment.CenterVertically),
          expanded = leadingExpanded,
          onCameraClick = onCameraClick,
          onAlbumClick = onAlbumClick,
          onExpandClick = onExpandChange,
        )
      } else {
        Spacer(modifier = Modifier.padding(start = 12.dp))
      }
      Column(
        modifier = Modifier
          .align(Alignment.CenterVertically)
          .padding(horizontal = 4.dp, vertical = 16.dp)
          .weight(1f),
      ) {
        if (imageUri != null) {
          AttachedImage(
            uri = imageUri,
            onRemoveClick = onRemoveImageClick,
          )
          Spacer(modifier = Modifier.height(16.dp))
        }
        Box {
          innerTextField()
          if (text.isEmpty()) {
            Text(
              text = stringResource(R.string.feature_chat_ui_message_gemini_placeholder),
              style = labelTextStyle.copy(color = labelTextStyle.color.copy(alpha = 0.7f)),
            )
          }
        }
      }
      Row(modifier = Modifier.align(Alignment.CenterVertically)) {
        when (trailingButtonState) {
          TrailingButtonState.Empty -> {
            // No content
          }

          TrailingButtonState.Send -> SendButton(onClick = { onSendClick(text) })

          TrailingButtonState.Stop -> {
            // TODO: 정지 버튼으로 바꾸기
            CircularProgressIndicator()
          }
        }
      }
    }
  }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun AttachedImage(uri: Uri, onRemoveClick: () -> Unit) {
  Box {
    GlideImage(
      modifier = Modifier
        .size(80.dp)
        .clip(MaterialTheme.shapes.small),
      model = uri,
      contentScale = ContentScale.Crop,
      contentDescription = null,
    )
    Icon(
      modifier = Modifier
        .align(Alignment.TopEnd)
        .clickable(onClick = onRemoveClick, role = Role.Button)
        .padding(4.dp),
      tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
      imageVector = Icons.Filled.RemoveCircle,
      contentDescription = "Remove attached image",
    )
  }
}

@Composable
private fun SendButton(modifier: Modifier = Modifier, onClick: () -> Unit) {
  IconButton(
    modifier = modifier,
    onClick = onClick,
  ) {
    Icon(
      modifier = Modifier.padding(8.dp),
      tint = MaterialTheme.colorScheme.onPrimaryContainer,
      imageVector = Icons.AutoMirrored.Filled.Send,
      contentDescription = "Send button",
    )
  }
}

@Composable
private fun LeadingButton(
  modifier: Modifier = Modifier,
  expanded: Boolean,
  onCameraClick: () -> Unit,
  onAlbumClick: () -> Unit,
  onExpandClick: (Boolean) -> Unit,
) {
  var collapsedButtonWidth by remember { mutableStateOf(0) }

  CrossFade(
    modifier = modifier.padding(start = 8.dp, end = 4.dp),
    showFirst = expanded,
    firstContent = {
      Row {
        SmallIconButton(
          imageVector = Icons.Outlined.PhotoCamera,
          contentDescription = "Camera button",
          tint = MaterialTheme.colorScheme.onPrimaryContainer,
          onClick = onCameraClick,
        )
        SmallIconButton(
          imageVector = Icons.Outlined.Image,
          contentDescription = "Image button",
          tint = MaterialTheme.colorScheme.onPrimaryContainer,
          onClick = onAlbumClick,
        )
      }
    },
    secondContent = {
      SmallIconButton(
        modifier = Modifier.onGloballyPositioned { coordinates ->
          collapsedButtonWidth = coordinates.size.width
        },
        imageVector = Icons.Filled.AddCircle,
        contentDescription = "Camera button",
        iconSize = 32.dp,
        tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
        onClick = { onExpandClick(!expanded) },
      )
    },
  )
}

@Composable
private fun SmallIconButton(
  modifier: Modifier = Modifier,
  iconSize: Dp = 24.dp,
  imageVector: ImageVector,
  contentDescription: String,
  tint: Color = MaterialTheme.colorScheme.onPrimaryContainer,
  onClick: () -> Unit,
) {
  Box(
    modifier = modifier
      .size(40.dp)
      .clickable(
        onClick = onClick,
        role = Role.Button,
        interactionSource = remember { MutableInteractionSource() },
        indication = rememberRipple(
          bounded = false,
          radius = 40.dp / 2,
        ),
      ),
  ) {
    Icon(
      modifier = Modifier
        .size(iconSize)
        .align(Alignment.Center),
      imageVector = imageVector,
      contentDescription = contentDescription,
      tint = tint,
    )
  }
}

@Composable
@Preview
private fun EmptyGeminiTextFieldPreview() {
  MaterialTheme {
    GeminiTextField(
      text = "",
      onTextChange = {},
      imageUri = null,
      leadingExpanded = false,
      trailingButtonState = TrailingButtonState.Send,
      onCameraClick = {},
      onAlbumClick = {},
      onSendClick = {},
      onExpandChange = {},
      onRemoveImageClick = {},
    )
  }
}

@Composable
@Preview
private fun GeminiTextFieldPreview() {
  MaterialTheme {
    GeminiTextField(
      text = "Text",
      onTextChange = {},
      imageUri = null,
      leadingExpanded = true,
      trailingButtonState = TrailingButtonState.Send,
      onCameraClick = {},
      onAlbumClick = {},
      onSendClick = {},
      onExpandChange = {},
      onRemoveImageClick = {},
    )
  }
}
