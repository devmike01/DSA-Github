package dev.gbenga.dsagithub.nav

import androidx.compose.runtime.Composable
import dev.gbenga.dsagithub.features.details.DetailScreen
import dev.gbenga.dsagithub.features.home.HomeScreen
import dev.gbenga.dsagithub.nav.choir.ChoirNavHost
import dev.gbenga.dsagithub.nav.choir.rememberChoir
import dev.gbenga.dsagithub.nav.choir.singNav

@Composable
fun AppNavHost(){

    val choirRoutes = rememberChoir()
    ChoirNavHost(choirRoutes, initialDestination=Home){
        singNav<Home> {
            HomeScreen(choirRoutes)
        }
        singNav<GithubDetails> {
            val gDetails = this.asRoute<GithubDetails>() ?: GithubDetails()
            DetailScreen(userId=gDetails.accountId, navController =choirRoutes )
        }

    }

//    val navController = rememberNavController()
//    NavHost(navController = navController, startDestination = Home){
//        composable<Home> {
//            HomeScreen(navController)
//        }
//        composable<GithubDetails> {
//            val gDetails = it.toRoute<GithubDetails>()
//            DetailScreen(userId=gDetails.accountId, navController = navController)
//        }
//    }
}
