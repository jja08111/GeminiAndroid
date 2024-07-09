package io.github.jja08111.core.navigation.mobile

import androidx.navigation.NavType
import androidx.navigation.navArgument

// TODO: Typesafe한 navigation으로 마이그레이션
sealed class MobileDestinations(val route: String)

data object RoomsMobileDestinations : MobileDestinations(route = "rooms")

data object ChatMobileDestinations : MobileDestinations(route = "chat") {
  const val CHAT_ID_ARG = "chatId"
  val arguments = listOf(
    navArgument(CHAT_ID_ARG) { type = NavType.StringType },
  )
  val routeWithArg: String = "$route/{${CHAT_ID_ARG}}"

  fun createRoute(roomId: String): String {
    return "$route/$roomId"
  }
}

data object SelectResponseDestinations : MobileDestinations(route = "select-response") {
  const val PROMPT_ID_ARG = "promptId"
  val arguments = listOf(
    navArgument(PROMPT_ID_ARG) { type = NavType.StringType },
  )
  val routeWithArg: String = "$route/{${PROMPT_ID_ARG}}"

  fun createRoute(promptId: String): String {
    return "$route/$promptId"
  }
}
