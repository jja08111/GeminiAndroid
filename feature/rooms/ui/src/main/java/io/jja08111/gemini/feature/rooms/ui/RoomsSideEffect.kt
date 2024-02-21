package io.jja08111.gemini.feature.rooms.ui

import androidx.annotation.StringRes

sealed class RoomsSideEffect {
  data class NavigateToChat(val roomId: String) : RoomsSideEffect()

  data class Message(
    @StringRes val messageRes: Int,
  ) : RoomsSideEffect()
}
