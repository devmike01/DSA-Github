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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import dev.gbenga.dsa.collections.CustomMap
import dev.gbenga.dsa.collections.HashMap
import dev.gbenga.dsa.collections.Stack
import dev.gbenga.dsa.collections.StackImpl
import dev.gbenga.dsagithub.nav.RouteCache
import dev.gbenga.dsagithub.nav.Screen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

enum class NavType{
    POPPED, ADD, IDLE, RESTORED
}

data class NavNode<K>(val key: K?=null,
                   val route: Any?=null,
                   val type: NavType = NavType.IDLE)

@Suppress("unchecked_cast")
@Composable
fun ChoirNavHost(choir : Choir, initialDestination: Any,
                 routeBuilder: @Composable Choir.() -> CustomMap<Any, Any>){

    val routes = routeBuilder(choir)

    var currentRoute = remember { mutableStateOf<Any?>(null) }
    var currentRouteKey by rememberSaveable { mutableStateOf<String?>(null) }
    val arguments = rememberSaveable { choir.argMap }

    LaunchedEffect(Unit) {
        val cachedRoute = arguments.getOrNull(currentRouteKey?.split(" ")[1])
        choir.restoreRoutes(arguments)
        println("currentRoute: $cachedRoute - $arguments")
        choir.navigate(cachedRoute ?: initialDestination)
    }

    LaunchedEffect(Unit) {
        choir.routes.collect { navNode ->

            Log.d("ChoirNavHost", "init route -> ${navNode.key} ${navNode.type}")
            when(navNode.type){
                NavType.POPPED -> {
                    choir.last()?.let {
                        currentRouteKey = it.key?.toString()
                        currentRoute.value = it.route // it.key?.toString()
                    }
                }
                NavType.ADD, NavType.RESTORED  -> {
                    currentRoute.value =  navNode.route
                    currentRouteKey = navNode.key?.toString()
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

    println("currentRouteNEW#: ${currentRoute.value}")
    // Display current route
    Crossfade(targetState = currentRoute.value) { targetState ->
        targetState?.let {
            (it as @Composable () -> Any).invoke() // compose
        }
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

    private val _sharedFlow = MutableSharedFlow<NavNode<*>>(replay = 1)
    val peekFlow: SharedFlow<NavNode<*>> get() = _sharedFlow.asSharedFlow()

    internal val stack = StackImpl<NavNode<*>>(capacity)

    fun lastOrNull() = stack.peek()


    fun silentPush(navNode: NavNode<*>){
        stack.push(navNode.copy(type = NavType.RESTORED))
    }

    // Push and notify the state
    fun pushNotify(navNode: NavNode<*>){
        val existingNode = stack.linearSearch { it.key == navNode.key }
        println("existingNode: $existingNode")
        ioCoroutine.launch {
            if (existingNode != null){
                _sharedFlow.emit(navNode.copy(
                    type = NavType.RESTORED))
            }else{
                stack.push(navNode)
                _sharedFlow.emit(navNode.copy(
                    type = NavType.ADD))
            }
        }

    }


    fun pushAllNotify(navNodeStack: Stack<NavNode<*>>){
        ioCoroutine.launch {
            while (navNodeStack.isNotEmpty()){
                stack.push(navNodeStack.pop().copy(type = NavType.RESTORED))
            }
            stack.peek()?.copy(
                type = NavType.RESTORED)?.let {
                pushNotify(it)
            }
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

class Choir() {


    companion object{
        const val INITIAL_ROUTE_CAPACITY = 10
    }

    var argMap : CustomMap<String?, Any> = HashMap() // key is object's qualifiedName name
    private val _routes : FlowNavNodeStack = FlowNavNodeStack(INITIAL_ROUTE_CAPACITY)
    val routes: SharedFlow<NavNode<*>> = _routes.peekFlow
    val registeredRoutes : CustomMap<Any, Any> = HashMap<Any, Any>()

    fun last() = _routes.lastOrNull()

    fun popBackStack(onLast: (() -> Unit)? = null){
        println("_routes_routes: ${_routes.stack}")
        _routes.popNotify(onLast=onLast)
    }

    fun restoreRoutes(routes: CustomMap<String?, Any>){
        routes.keys().forEach { key ->
            key?.let { // TODO: Works but arrangement is wrong and thus popped the wrong route
                val klazz = Class.forName(it).kotlin
                _routes.silentPush(NavNode(key = klazz,
                    route=registeredRoutes.getOrNull(klazz)))
            }
        }
        println("restoreRoutes ->${_routes.stack}")
    }

    internal inline fun <reified T: Any> asRoute(): T?{
        return  argMap.getOrNull(T::class.qualifiedName) as? T
    }


    fun <C: Any> navigate(route: C){
        registeredRoutes.getOrNull(route::class)?.let {
            argMap[route::class.qualifiedName] = route
            val navNode = NavNode(route::class, it)
            _routes.pushNotify(navNode)
        }

    }

    /**
     * @param page is a composable screen
     **/
    inline fun <reified klass: Any> putRoute(page: Any): Boolean{
        if (registeredRoutes.getOrNull(page) != null){
            return false
        }
        println("currentRouteNEW: $page")
        return registeredRoutes.put(klass::class, page)
    }


    fun cancel(){
        _routes.cancel()
    }

}

