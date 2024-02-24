package io.jja08111.gemini.feature.chat.ui.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
internal fun GotoBottomButton(modifier: Modifier = Modifier, onClick: () -> Unit) {
  SmallFloatingActionButton(
    modifier = modifier,
    containerColor = MaterialTheme.colorScheme.background,
    onClick = onClick,
  ) {
    Icon(
      imageVector = Icons.Default.KeyboardArrowDown,
      contentDescription = "Go to bottom",
      tint = MaterialTheme.colorScheme.primary,
    )
  }
}
