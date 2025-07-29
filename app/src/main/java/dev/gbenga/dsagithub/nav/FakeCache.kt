package dev.gbenga.dsagithub.nav

import dev.gbenga.dsa.collections.CustomMap
import dev.gbenga.dsa.collections.HashMap

// Fake cache of route
class FakeCache<V> private constructor() {

    private val routes : CustomMap<String, V> = HashMap()

    companion object{

        @Volatile
        private var instance: Any? =null

        fun <T> get(): FakeCache<T>{
            val fakeCache = instance ?: synchronized(this) {
                instance ?: FakeCache<T>().also { instance = it  }
            }
            return fakeCache as FakeCache<T>
        }
    }

    operator fun set(key: String, value: V){
        routes[key] = value
    }

    fun getOrNull(key: String): V? {
        return routes.getOrNull(key)
    }

    fun dispose(){
        routes.clear()
    }

}