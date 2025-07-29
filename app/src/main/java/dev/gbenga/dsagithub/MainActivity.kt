package dev.gbenga.dsagithub

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dev.gbenga.dsagithub.features.details.DetailScreen
import dev.gbenga.dsagithub.nav.AppNavHost
import dev.gbenga.dsagithub.nav.GithubDetails
import dev.gbenga.dsagithub.nav.Screen
import dev.gbenga.dsagithub.ui.theme.DSAGithubTheme

class MainActivity : ComponentActivity() {

    private val _bundle = Bundle()
    private val liveData = MutableLiveData<Bundle>()

    companion object{
        const val POP_BACKSTACK_BUNDLE = "MainActivity.POP_BACKSTACK_BUNDLE"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            DSAGithubTheme {
                AppNavHost()
            }
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBundle(POP_BACKSTACK_BUNDLE, _bundle)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        liveData.value = savedInstanceState.getBundle(POP_BACKSTACK_BUNDLE)
    }


}
