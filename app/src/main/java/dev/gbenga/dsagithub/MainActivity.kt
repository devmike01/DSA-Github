package dev.gbenga.dsagithub

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.os.bundleOf
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryOwner
import dev.gbenga.dsagithub.features.details.DetailScreen
import dev.gbenga.dsagithub.nav.AppNavHost
import dev.gbenga.dsagithub.nav.GithubDetails
import dev.gbenga.dsagithub.nav.Screen
import dev.gbenga.dsagithub.ui.theme.DSAGithubTheme

class MainActivity : ComponentActivity() {

    private val savedRegistry = SearchManager(this)
    private val liveData = MutableLiveData<Pair<String, String>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            DSAGithubTheme {
                AppNavHost()
            }
        }

    }



    fun setString(key: String="h", value: String){
        savedRegistry.setString(value)
        Log.d("MainActivity", "_bundle ---> $value")
    }

    override fun onResume() {
        super.onResume()
        Log.d("MainActivity", "_bundle --ddd-> ${savedRegistry.getQuery()}")
    }
}


class SearchManager(registryOwner: SavedStateRegistryOwner) : SavedStateRegistry.SavedStateProvider {
    companion object {
        private const val PROVIDER = "search_manager"
        private const val QUERY = "query"
    }

    private var query: String? = null


    fun setString(value: String){
        query = value
    }

    fun getQuery() = query

    init {
        // Register a LifecycleObserver for when the Lifecycle hits ON_CREATE
        registryOwner.lifecycle.addObserver(LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_CREATE) {
                val registry = registryOwner.savedStateRegistry

                // Register this object for future calls to saveState()
                registry.registerSavedStateProvider(PROVIDER, this)

                // Get the previously saved state and restore it
                val state = registry.consumeRestoredStateForKey(PROVIDER)

                // Apply the previously saved state
                query = state?.getString(QUERY)
            }
        })
    }

    override fun saveState(): Bundle {
        return bundleOf(QUERY to query)
    }

}