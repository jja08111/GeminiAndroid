package io.jja08111.gemini.mobile.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.github.jja08111.core.navigation.mobile.ChatMobileDestinations
import io.github.jja08111.core.navigation.mobile.RoomsMobileDestinations
import io.github.jja08111.core.navigation.mobile.SelectResponseDestinations
import io.github.jja08111.core.navigation.mobile.navigateToChat
import io.github.jja08111.core.navigation.mobile.navigateToSelectResponse
import io.jja08111.gemini.feature.chat.ui.ChatRoute
import io.jja08111.gemini.feature.chat.ui.select.response.SelectResponseRoute
import io.jja08111.gemini.feature.rooms.ui.RoomsRoute

@Composable
internal fun MobileNavHost(navController: NavHostController = rememberNavController()) {
  NavHost(
    navController = navController,
    startDestination = RoomsMobileDestinations.route,
  ) {
    composable(RoomsMobileDestinations.route) {
      RoomsRoute(
        navigateToChat = navController::navigateToChat,
        navigateToNewChat = navController::navigateToChat,
      )
    }
    composable(
      route = ChatMobileDestinations.routeWithArg,
      arguments = ChatMobileDestinations.arguments,
    ) {
      ChatRoute(
        popBackStack = navController::popBackStack,
        navigateToSelectResponse = navController::navigateToSelectResponse,
      )
    }
    composable(
      route = SelectResponseDestinations.routeWithArg,
      arguments = SelectResponseDestinations.arguments,
    ) {
      SelectResponseRoute(
        popBackStack = navController::popBackStack,
      )
    }
  }
}
