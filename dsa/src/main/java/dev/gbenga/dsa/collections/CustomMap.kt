package dev.gbenga.dsa.collections

import dev.gbenga.dsa.collections.list.LinkedList
import dev.gbenga.dsa.collections.list.LinkedListImpl
import kotlin.math.absoluteValue

interface CustomMap<K, V>: Collections{

    fun put(key: K, value: V)

    fun getOrNull(key: K): V?

    fun remove(key: K): Boolean

    fun values(): LinkedList<V>

    fun keys(): LinkedList<K>

    val size : Int
}

class HashMap<K, V> : CustomMap<K, V> {
    companion object{
        const val INITIAL_SIZE = 10
        const val LOAD_FACTOR = .75
    }


    internal data class Entry <K, V>(val key: K, var value: V)

    internal var buckets : Array<LinkedList<Entry<K, V>>?> = arrayOfNulls(INITIAL_SIZE)

    override var size = 0

    override fun put(key: K, value: V){
        var index = key.getIndex()
        if (buckets[index] == null){
            buckets[index] = LinkedListImpl()
        }
        val bucket = buckets[index]!!
        var updated : Boolean = false
        bucket.forEach { entry ->
            if (entry.key == key){
                entry.value = value
                updated = true
            }
        }

        if (updated){
            return
        }

        bucket.append(Entry(key, value))
        size++

        if (size > buckets.size * LOAD_FACTOR){
            resize()
        }
    }

    override fun getOrNull(key: K): V?{
        val index = key.getIndex()
        val bucket : LinkedList<Entry<K, V>>?  = buckets[index]
        return bucket?.linearSearch{ it.key == key }?.value
    }

    override fun remove(key: K): Boolean {
        val index = key.getIndex()
        val bucket = buckets[index]
        return bucket?.remove{ it?.key == key} == true
    }

    override fun values(): LinkedList<V> {
        val values = LinkedListImpl<V>()
        for (bucket in buckets){
            bucket?.forEach { entry ->
                values.append(entry.value)
            }
        }
        return values
    }

    override fun keys(): LinkedList<K> {
        val keys = LinkedListImpl<K>()
        for (bucket in buckets){
            bucket?.forEach { entry ->
                keys.append(entry.key)
            }
        }
        return keys
    }

    private fun resize() {
        val oldBuckets = buckets
        buckets = arrayOfNulls(oldBuckets.size * 2)
        size = 0

        oldBuckets.forEach { bucket ->
            bucket?.forEach {
                put(it.key, it.value)
            }
        }
    }

    fun <K> K.getIndex(): Int{
        return hashCode().absoluteValue.rem(buckets.size)
    }

    override fun toString(): String {
        val sb = StringBuilder()
        for (bucket in buckets){
            bucket?.forEach { item ->
                sb.append(item.let { "${it.key}: ${it.value}" })
                sb.append(", ")
            }
        }
        return "{$sb}"
    }

    override fun isEmpty(): Boolean {
        return size ==0
    }

    override fun isNotEmpty(): Boolean {
       return !isEmpty()
    }
}
