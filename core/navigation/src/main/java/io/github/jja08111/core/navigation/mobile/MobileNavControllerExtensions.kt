package io.github.jja08111.core.navigation.mobile

import androidx.navigation.NavHostController
import io.jja08111.gemini.model.createId

fun NavHostController.navigateToChat(roomId: String = createId()) {
  navigate(ChatMobileDestinations.createRoute(roomId))
}

fun NavHostController.navigateToSelectResponse(promptId: String) {
  navigate(SelectResponseDestinations.createRoute(promptId = promptId))
}
