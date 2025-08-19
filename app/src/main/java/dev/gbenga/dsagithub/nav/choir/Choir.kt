package dev.gbenga.dsagithub.nav.choir

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
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
import dev.gbenga.dsa.collections.Stack
import dev.gbenga.dsa.collections.StackImpl
import dev.gbenga.dsa.collections.list.LinkedList
import dev.gbenga.dsagithub.MainActivity
import dev.gbenga.dsagithub.base.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlin.reflect.KClass

enum class NavType{
    POPPED, ADD, IDLE, RESTORED
}

data class NavNode(
    val key: KClass<out Any>,
    val route: Any? =null,
    val type: NavType = NavType.IDLE): Comparable< NavNode>{
    override fun compareTo(other: NavNode): Int {
        return this.compareTo(other)
    }


}

@Suppress("unchecked_cast")
@Composable
fun ChoirNavHost(choir : Choir, initialDestination: Any,
                 routeBuilder: @Composable Choir.() -> CustomMap<Any, Any>){

    val routes = routeBuilder(choir)

    val activity = LocalActivity.current as MainActivity

    var currentRoute = remember { mutableStateOf<Any?>(null) }
    var currentRouteKey by rememberSaveable { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        activity.getRoutes()?.let {
            choir.restore(it)
        }
        choir.navigate(initialDestination)
    }

    LaunchedEffect(Unit) {
        choir.routes.collect { navNode ->
            val type = (navNode as NavNode).type

            when(type){
                NavType.POPPED -> {
                    val last = (choir.last() as? NavNode)
                    last?.let {
                        currentRouteKey = it.key.toString()
                        currentRoute.value = it.route
                    }
                }
                NavType.ADD, NavType.RESTORED  -> {
                    currentRoute.value =  navNode.route
                    currentRouteKey = navNode.key.toString().also { key ->
                        activity.setRoute(key)
                    }
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



class FlowNavNodeStack<K: Comparable<K>>(capacity: Int,
                       val ioCoroutine: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)) {

    private val _sharedFlow = MutableSharedFlow<NavNode>(replay = 1)
    val peekFlow: SharedFlow<NavNode> get() = _sharedFlow.asSharedFlow()

    internal val stack : Stack<NavNode> = StackImpl<NavNode>(capacity)
    private val queue = QueueImpl<NavNode>(capacity)

    fun peekOrNull() = stack.peek()


    fun silentPush(navNode: NavNode){
        stack.push(navNode.copy(type = NavType.RESTORED))
    }

    fun clear(){
        stack.clear()
    }

    // Push and notify the state
    fun pushNotify(navNode: NavNode){
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



class Choir() {


    companion object{
        const val CACHE_KEY = "Choir.CACHE_KEY"
        const val INITIAL_ROUTE_CAPACITY = 10
    }

    var argMap : CustomMap<String?, Any> = HashMap() // key is object's qualifiedName name
    private val _routes : FlowNavNodeStack<String> = FlowNavNodeStack(INITIAL_ROUTE_CAPACITY)
    val routes: SharedFlow<NavNode> = _routes.peekFlow
    val registeredRoutes : CustomMap<Any, Any> = HashMap()

    fun last() = _routes.peekOrNull()


    fun restore(routes: LinkedList<String>){
        if (routes.size() < 1) return
        val routeKey = Class.forName( routes.peekTailOrNull()!!.data)::class
        _routes.pushNotify(NavNode(
            key = routeKey,
            route = registeredRoutes.getOrNull(routeKey)))
        routes.forEach { key ->
            Logger.Holder.instance.d("LinkedList", key)
            val routeKey = Class.forName(key)::class
            _routes.silentPush(NavNode(
                key = routeKey,
                route = registeredRoutes.getOrNull(routeKey))
            )
        }
    }

    fun popBackStack(onLast: (() -> Unit)? = null){
        val route = _routes.stack.peek()?.key.toString()
        argMap.remove(route)
        _routes.popNotify(onLast=onLast)
    }

    internal inline fun <reified T: Any> asRoute(): T?{
        return  argMap.getOrNull(T::class.qualifiedName) as? T
    }


    fun navigate(route: Any){
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
        if (registeredRoutes.getOrNull(klass::class) != null){
            return false
        }
        return registeredRoutes.put(klass::class, page)
    }


    fun cancel(){
        _routes.cancel()

    }

}

