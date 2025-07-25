package dev.gbenga.dsagithub.nav

import dev.gbenga.dsa.collections.CustomMap
import dev.gbenga.dsa.collections.HashMap
import dev.gbenga.dsa.collections.list.LinkedListImpl
import dev.gbenga.dsagithub.nav.choir.NavNode

// Fake cache of route
class RouteCache<V> private constructor() {

    private val routes : CustomMap<String, V> = HashMap()

    companion object{

        @Volatile
        private var instance: Any? =null

        fun <T> get(): RouteCache<T>{
            val routeCache = instance ?: synchronized(this) {
                instance ?: RouteCache<T>().also { instance = it  }
            }
            return routeCache as RouteCache<T>
        }
    }

    operator fun set(key: String, value: V){
        routes[key] = value
    }

    fun getOrNull(key: String): V? {
        return routes.getOrNull(key)
    }

    fun dispose(){
        //routes.clear()
    }

}