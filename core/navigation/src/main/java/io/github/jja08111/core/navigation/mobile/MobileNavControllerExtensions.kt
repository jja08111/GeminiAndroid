package io.github.jja08111.core.navigation.mobile

import androidx.navigation.NavHostController
import java.util.UUID

fun NavHostController.navigateToChat(roomId: String = UUID.randomUUID().toString()) {
  navigate(ChatMobileDestinations.createRoute(roomId))
}
