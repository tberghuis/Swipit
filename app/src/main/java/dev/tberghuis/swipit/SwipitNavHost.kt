package dev.tberghuis.swipit

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable
import dev.tberghuis.swipit.home.Home
import dev.tberghuis.swipit.swiper.Swiper

@Composable
fun SwipitNavHost(
  modifier: Modifier = Modifier,
  navController: NavHostController = rememberNavController(),
  startDestination: String = "home"
) {
  NavHost(
    modifier = modifier, navController = navController, startDestination = startDestination
  ) {
    composable("home") {
      Home(navigateSubreddit = { subreddit ->
        navController.navigate("swiper/$subreddit")
      })
    }
    composable("swiper/{subreddit}") { backStackEntry ->
      Swiper()
    }
  }
}