package io.github.jja08111.core.navigation.mobile

import androidx.navigation.NavHostController

fun NavHostController.navigateToChat(roomId: String) {
  navigate(ChatMobileDestinations.createRoute(roomId))
}
