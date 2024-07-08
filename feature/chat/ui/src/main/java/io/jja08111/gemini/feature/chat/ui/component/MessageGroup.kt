package io.jja08111.gemini.feature.chat.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.jja08111.gemini.model.MessageGroup
import io.jja08111.gemini.model.ModelResponseState

@Composable
internal fun MessageGroup(modifier: Modifier = Modifier, messageGroup: MessageGroup) {
  val modelResponse = messageGroup.selectedResponse
  val modelResponseState = modelResponse.state

  Column(
    modifier = modifier,
  ) {
    Spacer(modifier = Modifier.padding(8.dp))
    PromptItem(text = messageGroup.prompt.text)
    Spacer(modifier = Modifier.padding(16.dp))
    ModelResponseItem(
      text = modelResponse.text,
      isLoading = modelResponseState == ModelResponseState.Generating,
      isError = modelResponseState == ModelResponseState.Error,
    )
    Spacer(modifier = Modifier.padding(8.dp))
  }
}
