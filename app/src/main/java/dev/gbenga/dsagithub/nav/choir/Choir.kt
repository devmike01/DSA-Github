package dev.gbenga.dsagithub.nav.choir

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import dev.gbenga.dsa.collections.CustomMap
import dev.gbenga.dsa.collections.HashMap
import dev.gbenga.dsa.collections.QueueImpl
import dev.gbenga.dsa.collections.StackImpl
import dev.gbenga.dsagithub.nav.FakeCache
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
    var isInitial by rememberSaveable { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        if (isInitial){
            choir.navigate(initialDestination)
            isInitial = false
        }else{
            choir.restore(routes)
        }
    }

    LaunchedEffect(Unit) {
        choir.routes.collect { navNode ->
            when(navNode.type){
                NavType.POPPED -> {
                    choir.last()?.let {
                        currentRouteKey = it.key?.toString()
                        currentRoute.value = it.route
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
    private val queue = QueueImpl<NavNode<*>>(capacity)

    fun peekOrNull() = stack.peek()


    fun silentPush(navNode: NavNode<*>){
        stack.push(navNode.copy(type = NavType.RESTORED))
    }

    fun clear(){
        stack.clear()
    }

    // Push and notify the state
    fun pushNotify(navNode: NavNode<*>){
        val existingNode = stack.linearSearch { it.key == navNode.key }
        println("existingNode: $existingNode")
        ioCoroutine.launch {
            if (existingNode != null){
                _sharedFlow.emit(navNode.copy(type = NavType.RESTORED))
            }else{
                stack.push(navNode)
                _sharedFlow.emit(navNode.copy(
                    type = NavType.ADD))
            }
        }

    }

    // Pop and notify the state
    fun popNotify(onLast: (() -> Unit)? = null,){
        println("existingStack:: $stack")
        if (stack.size() <= 1){
            if(onLast == null) {
                ioCoroutine.launch {
                    stack.peek()?.copy(type = NavType.POPPED)?.let {
                        _sharedFlow.emit(it)
                    }
                }
            }else{
                onLast.invoke()
            }
            return
        }
        val navNode = stack.pop()
        ioCoroutine.launch {
            _sharedFlow.emit(navNode.copy(type = NavType.POPPED))
        }
        println("existingStack: $stack")
    }

    fun cancel(){
        ioCoroutine.cancel()
    }
}



class Choir(private val fakeCache : FakeCache<CustomMap<String?, Any>> = FakeCache.get()) {


    companion object{
        const val CACHE_KEY = "Choir.CACHE_KEY"
        const val INITIAL_ROUTE_CAPACITY = 10
        const val LOAD_FACTOR = .75
    }

    var argMap : CustomMap<String?, Any> = HashMap() // key is object's qualifiedName name
    private val _routes : FlowNavNodeStack = FlowNavNodeStack(INITIAL_ROUTE_CAPACITY)
    val routes: SharedFlow<NavNode<*>> = _routes.peekFlow
    val registeredRoutes : CustomMap<Any, Any> = HashMap<Any, Any>()

    fun last() = _routes.peekOrNull()


    fun restore(routes: CustomMap<Any, Any>){
        val cachedArgs : CustomMap<String?, Any>? = fakeCache.getOrNull(CACHE_KEY)
        cachedArgs?.keys()?.apply { reverse() }?.forEach { cache ->
            cachedArgs.getOrNull(cache)?.let { value ->
                cache?.let {
                    argMap[cache] = value
                    val clazz = Class.forName(cache).kotlin
                    _routes.silentPush(NavNode(key=cache,
                        route = routes.getOrNull(clazz)))
                }
            }
        }
        _routes.stack.pop().let {
            Log.d("popBackStack", "${it.key} - ${it.route}")
            _routes.pushNotify(it)
        }
    }

    fun popBackStack(onLast: (() -> Unit)? = null){
        val route = _routes.stack.peek()?.key.toString()
        argMap.remove(route)
        _routes.popNotify(onLast=onLast)
        fakeCache[CACHE_KEY] = argMap
    }

    internal inline fun <reified T: Any> asRoute(): T?{
        return  argMap.getOrNull(T::class.qualifiedName) as? T
    }


    fun <C: Any> navigate(route: C){
        registeredRoutes.getOrNull(route::class)?.let {
            argMap[route::class.qualifiedName] = route
            fakeCache[CACHE_KEY] = argMap
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
        return registeredRoutes.put(klass::class, page)
    }


    fun cancel(){
        _routes.cancel()

    }

}

