package dev.gbenga.dsagithub.nav.choir

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import dev.gbenga.dsa.collections.CustomMap
import dev.gbenga.dsa.collections.HashMap
import dev.gbenga.dsa.collections.Stack
import dev.gbenga.dsa.collections.StackImpl
import dev.gbenga.dsa.collections.list.LinkedList
import dev.gbenga.dsa.collections.list.LinkedListImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor

enum class NavType{
    POPPED, ADD, IDLE
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
    var onCreateLifeCycle by rememberSaveable {mutableStateOf(false)}
    val lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
           when(event){
               Lifecycle.Event.ON_START -> {
                   onCreateLifeCycle = true
               }
               Lifecycle.Event.ON_STOP -> {
                   onCreateLifeCycle = false
                   choir.cancel()
               }
               else -> {}
           }
        }

        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(onCreateLifeCycle) {
        if (!onCreateLifeCycle)return@LaunchedEffect
        choir.setInitialRoute(initialDestination)
        println("onCreateLifeCycle -> $onCreateLifeCycle")
        choir.routes.collect {
            when(it.type){
                NavType.POPPED -> {
                    currentRoute.value = routes.values().lastOrNull()
                }
                NavType.ADD -> {
                    currentRoute.value = it.route
                }
                else ->{
                    Log.d("ChoirNavHost", "init route")
                }
            }
        }

    }


    // Display current route
    currentRoute.value?.let {
        (it as @Composable () -> Any).invoke() // compose
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
    println("new_route -> ${route}")
    putRoute<T>(route)
    return registeredRoutes
}



class FlowNavNodeStack(capacity: Int, val ioCoroutine: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)) {

    private val _sharedFlow = MutableSharedFlow<NavNode>()
    val flowStack: SharedFlow<NavNode> get() = _sharedFlow.asSharedFlow()
    internal val stack = StackImpl<NavNode>(10)


    // Push and notify the state
    fun pushNotify(navNode: NavNode){
        ioCoroutine.launch {
            stack.push(navNode)

            _sharedFlow.emit(navNode.copy(type = NavType.ADD))
        }

        println("route_stack: $stack")
    }


    // Pop and notify the state
    fun popNotify(){
        ioCoroutine.launch {
            _sharedFlow.emit(stack.pop().copy(type = NavType.POPPED))
        }
    }

    fun cancel(){
        ioCoroutine.cancel()
    }
}

class Choir() {

    val argumentMap = HashMap<Any, Any?>()
    val _routes : FlowNavNodeStack = FlowNavNodeStack(INITIAL_ROUTE_CAPACITY)
    val routes: SharedFlow<NavNode> = _routes.flowStack
    val registeredRoutes : CustomMap<Any, Any> = HashMap<Any, Any>()
    //fun getArg(): Any = navigationStack.

    companion object{

        const val INITIAL_ROUTE_CAPACITY = 10
    }


    fun setInitialRoute(key: Any){
        navigate(key)
    }

    fun popBackStack(){
        // Pop top stack
        _routes.popNotify()
    }

    inline fun <reified T: Any> asRoute(): T?{
        var arg: T? = null
        argumentMap.values().forEach {
            if (it is T){
                arg = it
            }
        }
        return arg
    }

    fun navigate(route: Any){
        registeredRoutes.getOrNull(route::class)?.let {
            println("registeredRoutes -> $it")
            val navNode = NavNode(route::class, it)
            _routes.pushNotify(navNode)
        }
    }

    inline fun <reified klass: Any> putRoute(page: Any){

        val clazz = Class.forName(klass::class.qualifiedName ?: "")
        val instance = clazz.getDeclaredConstructor().newInstance()
        val klazz = clazz.kotlin
        val primaryConstructor = klazz.primaryConstructor
        primaryConstructor?.parameters?.let { params ->
            for (param in params){
                val valueProperty = klazz.memberProperties.find { it.name == param.name }
                val value = valueProperty?.getter?.call(instance)
                argumentMap.put(klass::class, value)
            }
        }

        registeredRoutes.put(klass::class, page)
    }


    fun cancel(){
        _routes.cancel()
    }

}

