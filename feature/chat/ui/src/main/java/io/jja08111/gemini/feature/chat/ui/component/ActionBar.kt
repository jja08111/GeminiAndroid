package io.jja08111.gemini.feature.chat.ui.component

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.jja08111.gemini.core.ui.CrossFade
import io.jja08111.gemini.feature.chat.ui.R

internal enum class ActionBarTrailingButtonType {
  Empty,
  Send,
  Stop,
}

@Composable
internal fun ActionBar(
  modifier: Modifier = Modifier,
  inputMessage: String,
  trailingButtonType: ActionBarTrailingButtonType,
  leadingExpanded: Boolean = true,
  onExpandChange: (Boolean) -> Unit,
  onInputMessageChange: (String) -> Unit,
  onSendClick: (String) -> Unit,
  onCameraClick: () -> Unit,
  onImageClick: () -> Unit,
) {
  val interactionSource = remember { MutableInteractionSource() }

  LaunchedEffect(Unit) {
    interactionSource.interactions.collect { interaction ->
      if (interaction is PressInteraction.Release) {
        onExpandChange(false)
      }
    }
  }

  TextField(
    modifier = modifier.background(color = MaterialTheme.colorScheme.background),
    value = inputMessage,
    onValueChange = {
      onInputMessageChange(it)
      onExpandChange(false)
    },
    interactionSource = interactionSource,
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
    leadingIcon = {
      LeadingButton(
        expanded = leadingExpanded,
        onCameraClick = onCameraClick,
        onImageClick = onImageClick,
        onExpandClick = onExpandChange,
      )
    },
    trailingIcon = {
      Row {
        when (trailingButtonType) {
          ActionBarTrailingButtonType.Empty -> {
            // No content
          }

          ActionBarTrailingButtonType.Send -> SendButton(onClick = { onSendClick(inputMessage) })

          ActionBarTrailingButtonType.Stop -> {
            // TODO: 정지 버튼으로 바꾸기
            CircularProgressIndicator()
          }
        }
      }
    },
  )
}

@Composable
private fun SendButton(onClick: () -> Unit) {
  IconButton(
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

@Suppress("ktlint:standard:property-naming")
private const val ExpandedButtonDurationMillis = 200

private val ExpandedButtonEnterAnimationSpec = tween<Float>(
  delayMillis = ExpandedButtonDurationMillis / 4,
  durationMillis = ExpandedButtonDurationMillis / 2,
  easing = LinearOutSlowInEasing,
)

private val ExpandedButtonExitAnimationSpec = tween<Float>(
  durationMillis = ExpandedButtonDurationMillis / 2,
  easing = LinearOutSlowInEasing,
)

@Suppress("ktlint:standard:property-naming")
private const val ExpandedButtonInitialScale = 0.8f

@Suppress("ktlint:standard:property-naming")
private const val CollapseButtonDelayMillis = 50

@Suppress("ktlint:standard:property-naming")
private const val CollapseButtonDurationMillis = 200

private val CollapsedButtonEnterAnimationSpec = tween<Float>(
  delayMillis = CollapseButtonDelayMillis,
  durationMillis = CollapseButtonDurationMillis - CollapseButtonDelayMillis,
  easing = LinearOutSlowInEasing,
)

private val CollapsedButtonExitAnimationSpec = tween<Float>(
  durationMillis = CollapseButtonDurationMillis,
  easing = LinearOutSlowInEasing,
)

@Suppress("ktlint:standard:property-naming")
private const val CollapsedButtonInitialScale = 0.5f

@Composable
private fun LeadingButton(
  expanded: Boolean,
  onCameraClick: () -> Unit,
  onImageClick: () -> Unit,
  onExpandClick: (Boolean) -> Unit,
) {
  var collapsedButtonWidth by remember { mutableStateOf(0) }

  CrossFade(
    modifier = Modifier.padding(start = 8.dp, end = 4.dp),
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
          onClick = onImageClick,
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
private fun CollapsedActionBarPreview() {
  var leadingExpanded by remember { mutableStateOf(false) }

  MaterialTheme {
    ActionBar(
      modifier = Modifier.fillMaxWidth(),
      inputMessage = "",
      leadingExpanded = leadingExpanded,
      trailingButtonType = ActionBarTrailingButtonType.Send,
      onInputMessageChange = {},
      onCameraClick = {},
      onImageClick = {},
      onSendClick = {},
      onExpandChange = { leadingExpanded = it },
    )
  }
}

@Composable
@Preview
private fun ExpandedActionBarPreview() {
  var leadingExpanded by remember { mutableStateOf(true) }

  MaterialTheme {
    ActionBar(
      modifier = Modifier.fillMaxWidth(),
      inputMessage = "",
      leadingExpanded = leadingExpanded,
      trailingButtonType = ActionBarTrailingButtonType.Send,
      onInputMessageChange = {},
      onCameraClick = {},
      onImageClick = {},
      onSendClick = {},
      onExpandChange = { leadingExpanded = it },
    )
  }
}
