package dev.gbenga.dsagithub

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dev.gbenga.dsagithub.features.home.HomeScreen
import dev.gbenga.dsagithub.ui.theme.DSAGithubTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DSAGithubTheme {
                HomeScreen()
            }
        }
    }
}
