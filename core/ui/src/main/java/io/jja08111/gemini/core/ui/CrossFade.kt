package io.jja08111.gemini.core.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned

@Suppress("ktlint:standard:property-naming")
private const val AnimationDurationMillis = 200

private val EnterAnimationSpec = tween<Float>(
  delayMillis = AnimationDurationMillis / 4,
  durationMillis = AnimationDurationMillis / 2,
  easing = LinearOutSlowInEasing,
)

private val ExitAnimationSpec = tween<Float>(
  durationMillis = AnimationDurationMillis / 2,
  easing = LinearOutSlowInEasing,
)

@Suppress("ktlint:standard:property-naming")
private const val InitialContentAnimationScale = 0.8f

private fun enterAnimation(initialWidth: (fullWidth: Int) -> Int = { 0 }) =
  expandHorizontally(
    animationSpec = tween(durationMillis = AnimationDurationMillis),
    expandFrom = Alignment.Start,
    initialWidth = initialWidth,
    clip = false,
  ) + fadeIn(
    animationSpec = EnterAnimationSpec,
  ) + scaleIn(
    initialScale = InitialContentAnimationScale,
    animationSpec = EnterAnimationSpec,
  )

private fun exitAnimation(targetWidth: (fullWidth: Int) -> Int = { 0 }) =
  shrinkHorizontally(
    animationSpec = tween(durationMillis = AnimationDurationMillis),
    shrinkTowards = Alignment.Start,
    targetWidth = targetWidth,
    clip = false,
  ) + fadeOut(
    animationSpec = ExitAnimationSpec,
  ) + scaleOut(
    targetScale = InitialContentAnimationScale,
    animationSpec = ExitAnimationSpec,
  )

@Composable
fun CrossFade(
  modifier: Modifier = Modifier,
  showFirst: Boolean,
  firstContent: @Composable () -> Unit,
  secondContent: @Composable () -> Unit,
) {
  var firstContentWidth by remember { mutableStateOf(0) }
  var secondContentWidth by remember { mutableStateOf(0) }

  Box(modifier = modifier) {
    AnimatedVisibility(
      label = "FirstItemVisibility",
      visible = showFirst,
      enter = enterAnimation(initialWidth = { secondContentWidth }),
      exit = exitAnimation(targetWidth = { secondContentWidth }),
    ) {
      Box(
        modifier = Modifier.onGloballyPositioned { coordinates ->
          firstContentWidth = coordinates.size.width
        },
      ) {
        firstContent()
      }
    }
    AnimatedVisibility(
      label = "SecondItemVisibility",
      visible = !showFirst,
      enter = enterAnimation(initialWidth = { secondContentWidth }),
      exit = exitAnimation(targetWidth = { secondContentWidth }),
    ) {
      Box(
        modifier = Modifier.onGloballyPositioned { coordinates ->
          secondContentWidth = coordinates.size.width
        },
      ) {
        secondContent()
      }
    }
  }
}
