package dev.gbenga.dsagithub

import androidx.core.os.bundleOf
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.savedstate.SavedState
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryOwner

class AppStateRegistry(registryOwner: SavedStateRegistryOwner) : SavedStateRegistry.SavedStateProvider {

    var strPair: Pair<String, String?> = Pair("", null)

    companion object {
        private const val PROVIDER = "AppStateRegistry.saved_state_manager"
    }

    init {
        registryOwner.lifecycle.addObserver(LifecycleEventObserver{ _, event ->
            if(event == Lifecycle.Event.ON_CREATE){
                val registry = registryOwner.savedStateRegistry
                registry.registerSavedStateProvider(PROVIDER, this)
                val state = registry.consumeRestoredStateForKey(PROVIDER)
                if (strPair.first.isNotEmpty()){
                    strPair = strPair.copy(second = state?.getString(strPair.first))
                }
            }
        })
    }


    fun setString(key: String, value: String){
        strPair = Pair(key, value)
    }

    override fun saveState(): SavedState {
        return bundleOf(strPair.first to strPair.second)
    }


}