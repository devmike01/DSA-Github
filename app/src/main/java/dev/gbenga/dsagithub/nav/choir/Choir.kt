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
import dev.gbenga.dsa.collections.Stack
import dev.gbenga.dsa.collections.StackImpl
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
                 routeBuilder: @Composable Choir.() -> RouteMap<Any, Any>){
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
                    currentRoute.value = routes.values().peek()
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
inline fun <reified T: Any> Choir.singNav(noinline route: @Composable () -> Any) : RouteMap<Any, Any>{
    println("new_route -> ${route}")
    putRoute<T>(route)
    return registeredRoutes
}



class FlowNavNodeStack(capacity: Int, val ioCoroutine: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)): StackImpl<NavNode>(capacity) {

    private val _sharedFlow = MutableSharedFlow<NavNode>()
    val flowStack: SharedFlow<NavNode> get() = _sharedFlow.asSharedFlow()


    // Push and notify the state
    fun pushNotify(navNode: NavNode){
        ioCoroutine.launch {
            this@FlowNavNodeStack.push(navNode)

            _sharedFlow.emit(navNode.copy(type = NavType.ADD))
        }
    }


    // Pop and notify the state
    fun popNotify(){
        ioCoroutine.launch {
            _sharedFlow.emit(this@FlowNavNodeStack.pop().copy(type = NavType.POPPED))
        }
    }

    fun cancel(){
        ioCoroutine.cancel()
    }
}

class Choir() {


    val argumentMap = RouteMap<Any, Any?>()
    val _routes : FlowNavNodeStack = FlowNavNodeStack(INITIAL_ROUTE_CAPACITY)
    val routes: SharedFlow<NavNode> = _routes.flowStack
    val registeredRoutes = RouteMap<Any, Any>()
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

    //@Suppress("unchecked_cast")
    fun <T> getRecentRoute(): NavNode {
        return registeredRoutes.let { NavNode(it.keys().peek(), it.values().peek()) }
    }

    fun cancel(){
        _routes.cancel()
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
        navigationStack.forEach {

            temp.push(it.first)
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