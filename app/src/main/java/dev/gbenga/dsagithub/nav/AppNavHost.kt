package dev.gbenga.dsagithub.nav

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import dev.gbenga.dsagithub.features.details.DetailScreen
import dev.gbenga.dsagithub.features.home.HomeScreen
import kotlin.reflect.KClass

@Composable
fun AppNavHost(){
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Home){
        composable<Home> {
            HomeScreen(navController)
        }
        composable<GithubDetails> {
            val gDetails = it.toRoute<GithubDetails>()
            DetailScreen(userId=gDetails.accountId, navController = navController)
        }
    }
}
