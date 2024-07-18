package io.jja08111.gemini.feature.chat.ui.select.response

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.jja08111.gemini.feature.chat.ui.component.ExpandableModelResponseItem
import io.jja08111.gemini.feature.chat.ui.component.PromptItem
import io.jja08111.gemini.model.ModelResponseState

@Composable
internal fun SelectResponseScreen(
  uiState: SelectResponseUiState,
  snackbarHostState: SnackbarHostState,
  onBackClick: () -> Unit,
  onResponseClick: (responseId: String) -> Unit,
) {
  val prompt by uiState.promptStream.collectAsStateWithLifecycle(initialValue = null)
  val responses by uiState.responsesStream.collectAsStateWithLifecycle(initialValue = emptyList())

  Scaffold(
    snackbarHost = { SnackbarHost(snackbarHostState) },
    topBar = {
      TopAppBar(
        title = { Text(text = "Select response") },
        navigationIcon = {
          IconButton(onClick = onBackClick) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "Back button",
            )
          }
        },
      )
    },
  ) { innerPadding ->
    LazyColumn(
      modifier = Modifier.padding(innerPadding),
    ) {
      prompt?.let { prompt ->
        item {
          Box(modifier = Modifier.fillMaxWidth()) {
            PromptItem(
              modifier = Modifier
                .padding(all = 16.dp)
                .align(Alignment.CenterEnd),
              text = prompt.text,
              images = prompt.images,
            )
          }
        }
        items(count = responses.size, key = { responses[it].id }) { index ->
          val response = responses[index]
          var expanded by rememberSaveable { mutableStateOf(false) }

          ExpandableModelResponseItem(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            title = "Response #${index + 1}",
            text = response.text,
            expanded = expanded,
            isLoading = response.state == ModelResponseState.Generating,
            isError = response.state == ModelResponseState.Error,
            onExpandClick = { expanded = it },
            onClick = { onResponseClick(response.id) },
          )
        }
      }
    }
  }
}
