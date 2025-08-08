package dev.gbenga.dsagithub.nav

import dev.gbenga.dsa.collections.CustomMap
import dev.gbenga.dsa.collections.HashMap

// Fake cache of route
class FakeCache<V> {

    private val routes : CustomMap<String, V> = HashMap()

    companion object{

        val instance = mutableMapOf<Class<*>, FakeCache<*>>()

        @Suppress("UNCHECKED_CAST", "USELESS_CAST")
        inline fun <reified T> getInstance(): FakeCache<T>{
            return instance.getOrPut(T::class.java) {
                FakeCache<T>()
            } as FakeCache<T>
        }

        @Volatile
        private var newInstance: Any? =null

        @Deprecated("Bad for performance. ", replaceWith = ReplaceWith("Use getInstance(class)"))
        fun <T> get(): FakeCache<T>{
            val fakeCache = newInstance ?: synchronized(this) {
                newInstance ?: FakeCache<T>().also { newInstance = it  }
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