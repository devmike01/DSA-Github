package dev.gbenga.dsagithub

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dev.gbenga.dsagithub.features.details.DetailScreen
import dev.gbenga.dsagithub.nav.AppNavHost
import dev.gbenga.dsagithub.nav.GithubDetails
import dev.gbenga.dsagithub.nav.Screen
import dev.gbenga.dsagithub.ui.theme.DSAGithubTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DSAGithubTheme {
                AppNavHost()
            }
        }
    }
}
