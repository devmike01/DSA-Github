package dev.gbenga.dsagithub.nav

import dev.gbenga.dsa.collections.CustomMap
import dev.gbenga.dsa.collections.HashMap
import dev.gbenga.dsa.collections.list.LinkedListImpl
import dev.gbenga.dsagithub.nav.choir.NavNode

// Fake cache of route
class RouteCache private constructor() {

    private val routes : CustomMap<String, Any> = HashMap()

    companion object{

        @Volatile
        var instance: RouteCache? =null

        fun get(): RouteCache{
            return instance ?: synchronized(this) {
                instance ?: RouteCache().also { instance = it }
            }
        }
    }

    operator fun <V> set(key: String, value: V){
        routes[key] = value as Any
    }

    fun getOrNull(key: String): Any? {
        return routes.getOrNull(key)
    }

    fun dispose(){
        //routes.clear()
    }

}