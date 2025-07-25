package dev.gbenga.dsa.collections

import dev.gbenga.dsa.collections.list.LinkedList
import dev.gbenga.dsa.collections.list.LinkedListImpl
import dev.gbenga.dsa.collections.list.toArray


interface Stack<T>: Collections<T>{
    fun pop(): T

    fun push(value: T)

    fun peek(): T?

    fun size(): Int

    fun forEach(onEach: (T) -> Unit)

    fun isFull(): Boolean

    fun toList(): List<T>

    fun clear()
}

open class StackImpl<T>(private var capacity: Int) : Stack<T> {

    private var linkedList = LinkedListImpl<T>()

    private var itemCount: Int = 0

    private fun checkCapacity(block: () -> Unit){
        if (itemCount < capacity){
            return block()
        }
        throw StackOverflowError("Stack is full. count: $itemCount, size: ${size()}")
    }

    override fun size() = itemCount

    override fun forEach(onEach: (T) -> Unit) {
        linkedList.forEach (onEach)
    }

    override fun isFull(): Boolean {
        return itemCount >= size()
    }

    override fun toList(): List<T> {
        val list = mutableListOf<T>()
        linkedList.forEach {
            list.add(it)
        }
        return list
    }

    override fun clear() {
        linkedList.clear()
    }


    override fun pop(): T {
        return linkedList.removeHead()?.also {
            itemCount -= 1
        }  ?: throw UnderflowError("Cannot pop from an empty stack")
    }

    override fun peek(): T?  {
        return linkedList.peekHead() //?: throw EmptyStackException()
    }

    override fun isEmpty(): Boolean = itemCount == 0

    override fun isNotEmpty(): Boolean {
        return !isEmpty()
    }

    override fun bubbleSort(predicate: (T) -> Boolean): T? = linkedList.bubbleSort(predicate)

    override fun linearSearch(predicate: (T) -> Boolean): T? {
        return linkedList.linearSearch (predicate)
    }

    override fun remove(predicate: (T?) -> Boolean): Boolean = linkedList.remove(predicate)

    override fun push(value: T) = checkCapacity {
        // A, B, C
        itemCount += 1
        linkedList.prepend(value)
    }

    override fun toString(): String {
        val sb = StringBuffer()
        var _count = 0
        linkedList.forEach{
            _count += 1
            sb.append(it)
            if (_count < itemCount){
                sb.append(",")
            }
        }
        return "[$sb]"
    }


}