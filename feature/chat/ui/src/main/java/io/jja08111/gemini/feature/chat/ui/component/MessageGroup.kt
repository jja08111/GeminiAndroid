package io.jja08111.gemini.feature.chat.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.jja08111.gemini.model.MessageGroup
import io.jja08111.gemini.model.ModelResponse
import io.jja08111.gemini.model.ModelResponseState

@Composable
internal fun MessageGroup(
  modifier: Modifier = Modifier,
  messageGroup: MessageGroup,
  onModelResponseLongClick: (ModelResponse) -> Unit,
) {
  val prompt = messageGroup.prompt
  val modelResponse = messageGroup.selectedResponse
  val modelResponseState = modelResponse.state

  Column(
    modifier = modifier,
  ) {
    PromptItem(
      modifier = Modifier
        .padding(all = 16.dp)
        .padding(start = PromptItemHorizontalMargin)
        .align(alignment = Alignment.End),
      text = prompt.text,
      images = prompt.images,
    )
    ModelResponseItem(
      modifier = Modifier.padding(all = 16.dp),
      text = modelResponse.text,
      isLoading = modelResponseState == ModelResponseState.Generating,
      isError = modelResponseState == ModelResponseState.Error,
      onLongClick = { onModelResponseLongClick(modelResponse) },
    )
  }
}
