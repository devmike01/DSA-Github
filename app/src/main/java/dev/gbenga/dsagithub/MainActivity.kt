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
import dev.gbenga.dsa.collections.list.LinkedList
import dev.gbenga.dsa.collections.list.LinkedListImpl
import dev.gbenga.dsa.collections.list.toArray
import dev.gbenga.dsa.collections.list.toMyLinkedList
import dev.gbenga.dsagithub.SearchManager.Companion.ROUTES
import dev.gbenga.dsagithub.features.details.DetailScreen
import dev.gbenga.dsagithub.nav.AppNavHost
import dev.gbenga.dsagithub.nav.GithubDetails
import dev.gbenga.dsagithub.nav.Screen
import dev.gbenga.dsagithub.ui.theme.DSAGithubTheme
import kotlin.reflect.KClass

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

    fun setRoute(route: String){
        savedRegistry.setRoutes(route)
    }

    fun getRoutes(): LinkedList<String>? {
        return savedRegistry.getRoutes()
    }

}


class SearchManager(registryOwner: SavedStateRegistryOwner) : SavedStateRegistry.SavedStateProvider {
    companion object {
        private const val PROVIDER = "search_manager"
        private const val ROUTES = "ROUTES"
    }

    private var screenRouteKeys: LinkedList<String>? = LinkedListImpl<String>()

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
                screenRouteKeys = state?.getStringArray(ROUTES)?.toMyLinkedList()
            }
        })
    }

    fun setRoutes(route: String){
        if (screenRouteKeys?.linearSearch(route) == null){
            screenRouteKeys?.append(route)
        }
    }

    fun getRoutes(): LinkedList<String>?{
        return screenRouteKeys
    }

    override fun saveState(): Bundle {
        return bundleOf(ROUTES to screenRouteKeys?.toArray())
    }

}
