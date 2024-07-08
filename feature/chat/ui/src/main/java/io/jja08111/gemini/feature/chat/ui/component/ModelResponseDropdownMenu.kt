package io.jja08111.gemini.feature.chat.ui.component

import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpOffset
import io.jja08111.gemini.model.ModelResponse

@Composable
internal fun rememberModelResponseDropdownMenuState(): ModelResponseDropdownMenuState {
  return remember { ModelResponseDropdownMenuState() }
}

@Stable
internal class ModelResponseDropdownMenuState {
  private var pressPosition by mutableStateOf(Offset.Zero)

  var selectedModelResponse by mutableStateOf<ModelResponse?>(null)
    private set

  val expanded: Boolean
    get() = selectedModelResponse != null

  val offset: DpOffset
    @Composable
    get() {
      val density = LocalDensity.current
      return with(density) {
        val xDp = pressPosition.x.toDp()
        val yDp = pressPosition.y.toDp()
        DpOffset(xDp, yDp)
      }
    }

  context(PointerInputScope)
  suspend fun awaitPressEvent() {
    awaitPointerEventScope {
      while (true) {
        val event = awaitPointerEvent()
        if (event.type == PointerEventType.Press) {
          event.changes.firstOrNull()?.position?.let(
            ::updatePressPosition,
          )
        }
      }
    }
  }

  private fun updatePressPosition(offset: Offset) {
    pressPosition = offset
  }

  fun expand(modelResponse: ModelResponse) {
    selectedModelResponse = modelResponse
  }

  fun hide() {
    selectedModelResponse = null
  }
}

@Composable
internal fun BoxWithConstraintsScope.ModelResponseDropdownMenu(
  modifier: Modifier = Modifier,
  state: ModelResponseDropdownMenuState = rememberModelResponseDropdownMenuState(),
  onDismissRequest: () -> Unit,
  onRegenerateClick: () -> Unit,
) {
  DropdownMenu(
    modifier = modifier,
    offset = state.offset.copy(y = -maxHeight + state.offset.y),
    expanded = state.expanded,
    onDismissRequest = onDismissRequest,
  ) {
    DropdownMenuItem(
      text = { Text("Regenerate") },
      onClick = onRegenerateClick,
    )
  }
}
