package dev.gbenga.dsagithub.nav

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.gbenga.dsagithub.features.home.HomeScreen

@Composable
fun AppNavHost(screens: Any){
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Home){
        composable<Home> {
            HomeScreen()
        }
    }
}
