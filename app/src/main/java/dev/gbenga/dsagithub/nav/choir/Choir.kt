package dev.gbenga.dsagithub.nav.choir

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import dev.gbenga.dsa.collections.CustomMap
import dev.gbenga.dsa.collections.HashMap
import dev.gbenga.dsa.collections.StackImpl
import dev.gbenga.dsagithub.nav.GithubDetails
import dev.gbenga.dsagithub.nav.RouteCache
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.io.Serializable

enum class NavType{
    POPPED, ADD, IDLE, RESTORED
}

data class NavNode(val key: Any?=null,
                   val route: Any?=null,
                   val type: NavType = NavType.IDLE)

@Suppress("unchecked_cast")
@Composable
fun ChoirNavHost(choir : Choir, initialDestination: Any,
                 routeBuilder: @Composable Choir.() -> CustomMap<Any, Any>){

    val routes = routeBuilder(choir)

    var currentRoute = remember { mutableStateOf<Any?>(null) }

    LaunchedEffect(Unit) {
        choir.setInitialRoute(initialDestination)
    }

    LaunchedEffect(Unit) {
        choir.routes.collect {
            when(it.type){
                NavType.POPPED -> {
                    choir.last()?.route?.let { route ->
                        currentRoute.value = route  //it.route
                    }
                }
                NavType.ADD -> {
                    currentRoute.value =  it.route
                }
                else ->{
                    Log.d("ChoirNavHost", "init route")
                }
            }
        }

    }

    DisposableEffect(Unit) {
        onDispose {
            choir.cancel()
        }
    }

    // Display current route
    Crossfade(targetState = currentRoute.value) { targetState ->
        targetState?.let {
            (it as @Composable () -> Any).invoke() // compose
        } ?: println("currentRoute -> $targetState")
    }


    // Handle back stack
    BackHandler {
        choir.popBackStack()
    }
}

@Composable
fun rememberChoir(): Choir{
    return remember { Choir()}
}


@Composable
inline fun <reified T: Any> Choir.singNav(noinline route: @Composable () -> Any) : CustomMap<Any, Any>{
    remember { putRoute<T>(route) }
    return registeredRoutes
}



class FlowNavNodeStack(capacity: Int,
                       val ioCoroutine: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)) {

    private val _sharedFlow = MutableSharedFlow<NavNode>(replay = 1)
    val flowStack: SharedFlow<NavNode> get() = _sharedFlow.asSharedFlow()

    internal val stack = StackImpl<NavNode>(capacity)

    fun lastOrNull() = stack.peek()

    // Push and notify the state
    fun pushNotify(navNode: NavNode){
        ioCoroutine.launch {
            stack.push(navNode)
            _sharedFlow.emit(navNode.copy(
                type = NavType.ADD))
        }

    }


    // Pop and notify the state
    fun popNotify(onLast: (() -> Unit)? = null,){
        if (stack.size() <= 1){
            onLast?.invoke()
            return
        }
        val navNode = stack.pop()
        ioCoroutine.launch {
            _sharedFlow.emit(navNode.copy(type = NavType.POPPED))
        }
    }

    fun cancel(){
        ioCoroutine.cancel()
    }
}

class Choir(private val routeCache: RouteCache= RouteCache.get()) {


    companion object{
        const val INITIAL_ROUTE_CAPACITY = 10
        var isInitial : Boolean = false
    }


    var argStack = StackImpl<Any>(INITIAL_ROUTE_CAPACITY)
    private val _routes : FlowNavNodeStack = FlowNavNodeStack(INITIAL_ROUTE_CAPACITY)
    val routes: SharedFlow<NavNode> = _routes.flowStack
    val registeredRoutes : CustomMap<Any, Any> = HashMap<Any, Any>()

    fun last() = _routes.lastOrNull()


    fun popBackStack(onLast: (() -> Unit)? = null){
        println("setInitialRoute: POPPED")
        _routes.popNotify(onLast=onLast)
    }

    internal inline fun <reified T: Any> asRoute(): T?{
        val temp = StackImpl<Any>(INITIAL_ROUTE_CAPACITY)
        while (argStack.isNotEmpty()){
            val routes = argStack.pop().also {
                temp.push(it)
            }
            if (routes is T){
                return routes
            }
        }
        argStack = temp
        return  null
    }

    fun <C: Any> setInitialRoute(route: C){

        println("setInitialRoute: ${_routes.lastOrNull()} ${routeCache.getOrNull("route")}")
        navigate(_routes.lastOrNull() ?: route)
    }

    fun <C: Any> navigate(route: C){
        registeredRoutes.getOrNull(route::class)?.let {
            argStack.push(route)
            val navNode = NavNode(route::class, it)
            _routes.pushNotify(navNode)
            routeCache["route"] = _routes.stack
        }

    }

    inline fun <reified klass: Any> putRoute(page: Any): Boolean{
        if (registeredRoutes.getOrNull(page) != null){
            return false
        }
        return registeredRoutes.put(klass::class, page)
    }


    fun cancel(){
        _routes.cancel()
    }

}

