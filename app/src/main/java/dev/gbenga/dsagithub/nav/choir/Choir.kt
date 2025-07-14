package dev.gbenga.dsagithub.nav.choir

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.gbenga.dsa.collections.Stack
import dev.gbenga.dsa.collections.StackImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlin.coroutines.CoroutineContext
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor


data class NavNode(val key: Any?=null, val route: Any=Any())

@Suppress("unchecked_cast")
@Composable
fun ChoirNavHost(choir : Choir, initialDestination: Any,
                 routeBuilder: @Composable Choir.() -> StateFlow<RouteMap<Any, Any>>){
    var currentNavNode = remember { mutableStateOf(NavNode()) }
    LaunchedEffect(choir.currentRoute) {
        choir.currentRoute.collect {
            currentNavNode.value = it
        }
    }

    val routeWithLifeCycle = routeBuilder(choir).collectAsStateWithLifecycle()
    currentNavNode.value.key?.let {
        (currentNavNode.value.route as @Composable () -> Any).invoke() // compose
    } ?: routeWithLifeCycle.value.getOrNull(initialDestination::class).let {
        it as @Composable () -> Any
    }.invoke()

    BackHandler {
        println("ChoirNavHost -> ChoirNavHost")
        choir.popBackStack()
    }
}

@Composable
fun rememberChoir(): Choir{
    return remember { Choir()}
}

@Composable
inline fun <reified T: Any> Choir.singNav(noinline route: @Composable () -> Any): StateFlow<RouteMap<Any, Any>>{
    println("new_route -> ${route}")
    putRoute<T>(route)
    return routeMap
}


class Choir(val ioCoroutine: CoroutineScope = CoroutineScope(Dispatchers.IO)) {

    val argumentMap = RouteMap<Any, Any?>()
    private val _currentRoute = MutableSharedFlow<NavNode>() //NavNode
    val currentRoute = _currentRoute.asSharedFlow()
    val routeMap = MutableStateFlow(RouteMap<Any, Any>())
    //fun getArg(): Any = navigationStack.

    init {

        ioCoroutine.launch {
            _currentRoute.asSharedFlow().collect {
                println("CoroutineScope@1: $it")
            }
        }
    }

    fun popBackStack(){
        // Pop top stack
        ioCoroutine.launch {
            println("ROUTE_r ${routeMap.value.keys()}")
//            val navNode = routeMap.value.let {
//                NavNode(key =it.keys().pop(), route =  it.values().pop())
//            }
//            _currentRoute.emit(navNode)
        }
    }

    inline fun <reified T: Any> asRoute(): T?{
        var values = argumentMap.values()
        while (values.isNotEmpty()){
            val popped = values.pop()
            if (popped is T){
                return popped
            }

        }
        return null
    }

    fun navigate(route: Any){
        routeMap.value.getOrNull(route::class)?.let { route ->
            println("navigate to $route")
            ioCoroutine.launch {
                _currentRoute.emit(NavNode(
                    key = route,
                    route =route
                ))
            }
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

        routeMap.update {
            it.apply { put(klass::class, page) }
        }
    }

    //@Suppress("unchecked_cast")
    fun <T> getRecentRoute(): SharedFlow<NavNode> {
        ioCoroutine.launch {
            routeMap.collect { routes ->
                val rPair = routes.let {
                    it.keys().peek() to it.values().peek()
                }
                _currentRoute.tryEmit(NavNode(key = rPair.first, route = rPair.second))
            }
        }
       return _currentRoute.asSharedFlow()
    }

}



@Serializable
class RouteMap <K, V>() {

    companion object{
        const val INITIAL_CAPACITY = 10
    }

    private var navigationStack: Stack<Pair<K, V>> = StackImpl<Pair<K, V>>(INITIAL_CAPACITY)

    fun put(key: K, value: V){
        val newSize = if (navigationStack.isFull()){
            navigationStack.size() * 2
        }else{
            navigationStack.size()
        }
        val tempStack : Stack<Pair<K, V>> =  StackImpl<Pair<K, V>>(newSize)
        var found = false
        while (navigationStack.isNotEmpty()){
            val (k, v) = navigationStack.pop()
            if (key == k){
                found = true
            }else{
                tempStack.push(k to v)
            }
        }

        while (tempStack.isNotEmpty()){
            navigationStack.push(tempStack.pop())
        }

        navigationStack.push(key to value)
        Log.d("navigatio001", "navigationStack->" +
                "$navigationStack")
    }

    private fun updateCapacity(count: Int, tempStack : Stack<Pair<K, V>>,
                               onCopy: (Stack<Pair<K, V>>) -> Unit){
        if (count >= tempStack.size()){
            // update capacity
            val temp = StackImpl<Pair<K, V>>(tempStack.size()*2)
            // copy stack content
            while (tempStack.isNotEmpty()){
                temp.push(tempStack.pop())
            }

            onCopy(temp)
            //tempStack = temp
        }
    }

    fun getOrNull(key: K): V?{
        var tempStack : Stack<Pair<K, V>> = StackImpl<Pair<K, V>>(navigationStack.size())
        var value: V? = null
        while (navigationStack.isNotEmpty()){
            val (k, v) = navigationStack.pop()
            tempStack.push(k to v)
            if (k == key){
                value = v;
                navigationStack = tempStack
                return value
            }
        }
        while (tempStack.isNotEmpty()){
            tempStack.push(tempStack.pop())
        }
        Log.d("getOrNull", "Breaking -> $tempStack $key: $value")
        return value
    }

    @Suppress("unchecked_cast")
    fun <K, R> mapKeys(block: (K) -> R): Stack<R>{
        val  temp = StackImpl<R>(INITIAL_CAPACITY)
        while (navigationStack.isNotEmpty()){
            temp.push(block(navigationStack.pop() as K))
        }
        return temp
    }

    fun keys(): Stack<K>{
        val temp = StackImpl<K>(navigationStack.size())
        println("Stack_keys#1: $navigationStack")
        while (navigationStack.isNotEmpty()){
            temp.push(navigationStack.pop().first)
        }
        println("Stack_keys: $temp")
        return temp
    }

    fun values(): Stack<V>{
        val temp = StackImpl<V>(navigationStack.size())
        while (navigationStack.isNotEmpty()){
            temp.push(navigationStack.pop().second)
        }
        return temp
    }

    fun remove(key: K){
        var temp = StackImpl<Pair<K, V>>(navigationStack.size())
        while (navigationStack.isNotEmpty()){
            val popped = navigationStack.pop()
            if (popped != key){
                temp.push(popped)
            }
        }
        navigationStack = temp
    }

    override fun toString(): String {
        val sb = StringBuilder()
        navigationStack.forEach {
            sb.append("${it.first} : ${it.second}")
            sb.append(",")
        }
        return "[$sb]"
    }
}