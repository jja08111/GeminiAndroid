package io.jja08111.gemini.feature.rooms.ui

import android.text.format.DateUtils
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import io.jja08111.gemini.model.Room
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
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
      LazyColumn(modifier = Modifier.fillMaxSize()) {
        when (val refreshState = loadState.refresh) {
          is LoadState.Loading -> {
            items(count = 5) { RoomTileSkeleton() }
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

          is LoadState.NotLoading -> {
            items(count = rooms.itemCount) { index ->
              val room = checkNotNull(rooms[index])
              RoomTile(
                modifier = Modifier.fillMaxWidth(),
                room = room,
                onClick = { onRoomClick(room.id) },
              )
            }
          }
        }
        when (val appendState = loadState.append) {
          is LoadState.Loading -> {
            item { RoomTileSkeleton() }
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

          is LoadState.NotLoading -> {}
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

@Stable
private fun Modifier.roomTilePadding(): Modifier {
  return this.padding(horizontal = 16.dp, vertical = 12.dp)
}

// TODO: Show summarized user message.
@Composable
internal fun RoomTile(modifier: Modifier = Modifier, room: Room, onClick: () -> Unit) {
  Column(
    modifier = modifier
      .clickable(onClick = onClick)
      .roomTilePadding(),
  ) {
    Text(
      text = room.title ?: "New chat",
      maxLines = 2,
      overflow = TextOverflow.Ellipsis,
      style = MaterialTheme.typography.titleMedium.copy(
        color = MaterialTheme.colorScheme.onBackground,
      ),
    )
    Text(
      text = room.activatedAt.toTimeSpanText(),
      style = MaterialTheme.typography.labelLarge.copy(
        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
      ),
    )
  }
}

@Composable
private fun Modifier.skeletonStyle(): Modifier {
  return this
    .clip(shape = MaterialTheme.shapes.extraSmall)
    .background(color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f))
}

@Composable
private fun RoomTileSkeleton() {
  Column(modifier = Modifier.roomTilePadding()) {
    Box(
      modifier = Modifier
        .size(width = 184.dp, height = 20.dp)
        .skeletonStyle(),
    )
    Spacer(modifier = Modifier.height(8.dp))
    Box(
      modifier = Modifier
        .size(width = 60.dp, height = 16.dp)
        .skeletonStyle(),
    )
  }
}

private fun LocalDateTime.toTimeSpanText(): String {
  val now = Instant.now().toEpochMilli()
  val defaultZone = ZoneId.systemDefault()
  return DateUtils.getRelativeTimeSpanString(
    this.atZone(defaultZone).toInstant().toEpochMilli(),
    now,
    DateUtils.DAY_IN_MILLIS,
  ).toString()
}

@Composable
@Preview(showBackground = true)
private fun RoomTileSkeletonPreview() {
  MaterialTheme {
    RoomTileSkeleton()
  }
}
