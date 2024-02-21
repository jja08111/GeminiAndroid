package io.jja08111.gemini.feature.rooms.ui

import android.text.format.DateUtils
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import io.jja08111.gemini.model.Room
import io.jja08111.gemini.model.TextContent
import java.util.Date
import kotlin.math.abs

@Composable
fun RoomsScreen(
  uiState: RoomsUiState,
  snackbarHostState: SnackbarHostState,
  onRoomClick: (String) -> Unit,
  onCreateClick: () -> Unit,
) {
  val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

  Scaffold(
    modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
    topBar = { RoomTopBar(scrollBehavior = scrollBehavior) },
    snackbarHost = { SnackbarHost(snackbarHostState) },
  ) {
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(it),
    ) {
      val rooms = uiState.roomStream.collectAsLazyPagingItems()
      val loadState = rooms.loadState
      LazyColumn {
        items(count = rooms.itemCount) { index ->
          val room = checkNotNull(rooms[index])
          RoomTile(
            modifier = Modifier.fillMaxWidth(),
            room = room,
            onClick = { onRoomClick(room.id) },
          )
        }
        when (val refreshState = loadState.refresh) {
          is LoadState.Loading -> {
            item { CircularProgressIndicator() }
          }

          is LoadState.Error -> {
            item {
              Text(
                modifier = Modifier.fillParentMaxSize(),
                text = refreshState.error.localizedMessage ?: stringResource(
                  R.string.feature_rooms_ui_failed_to_load,
                ),
              )
            }
          }

          else -> {}
        }
        when (val appendState = loadState.append) {
          is LoadState.Loading -> {
            item { CircularProgressIndicator() }
          }

          is LoadState.Error -> {
            item {
              Text(
                modifier = Modifier,
                text = appendState.error.localizedMessage ?: stringResource(
                  R.string.feature_rooms_ui_failed_to_load,
                ),
              )
            }
          }

          else -> {}
        }
      }
      FloatingActionButton(
        modifier = Modifier
          .align(Alignment.BottomEnd)
          .padding(24.dp),
        onClick = onCreateClick,
      ) {
        Icon(imageVector = Icons.Filled.Add, contentDescription = "Create a chat room")
      }
    }
  }
}

@Composable
internal fun RoomTopBar(
  scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(),
) {
  LargeTopAppBar(
    modifier = Modifier.verticalScrollDisabled(),
    title = {
      Text(text = "Gemini")
    },
    scrollBehavior = scrollBehavior,
  )
}

fun Modifier.verticalScrollDisabled() =
  pointerInput(Unit) {
    awaitPointerEventScope {
      while (true) {
        awaitPointerEvent(pass = PointerEventPass.Initial).changes.forEach {
          val offset = it.positionChange()
          if (abs(offset.y) > 0f) {
            it.consume()
          }
        }
      }
    }
  }

@Composable
internal fun RoomTile(modifier: Modifier = Modifier, room: Room, onClick: () -> Unit) {
  val recentMessage = room.recentMessage
  Column(
    modifier = modifier
      .clickable(onClick = onClick)
      .padding(horizontal = 16.dp, vertical = 12.dp),
  ) {
    Text(
      text = when (val content = recentMessage?.content) {
        is TextContent -> content.text
        null -> "New chat"
      },
      maxLines = 2,
      overflow = TextOverflow.Ellipsis,
      style = MaterialTheme.typography.titleMedium.copy(
        color = MaterialTheme.colorScheme.onBackground,
      ),
    )
    Text(
      text = (recentMessage?.createdAt ?: room.createdAt).toTimeSpanText(),
      style = MaterialTheme.typography.labelLarge.copy(
        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
      ),
    )
  }
}

fun Date.toTimeSpanText(): String {
  val now = Date().time
  return DateUtils.getRelativeTimeSpanString(time, now, DateUtils.DAY_IN_MILLIS).toString()
}
