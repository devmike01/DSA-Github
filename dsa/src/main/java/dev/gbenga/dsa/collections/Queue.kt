package dev.gbenga.dsa.collections

import dev.gbenga.dsa.collections.list.LinkedList
import dev.gbenga.dsa.collections.list.Node


interface Queue<T> {

    fun dequeue(): T

    fun enqueue(data: T)

    fun isEmpty(): Boolean

}

class QueueImpl<T>(private val capacity: Int) : Queue<T>{

    private val linkedList = LinkedList<T>()

    private var count = 0

    override fun dequeue(): T {
        return linkedList.peekHead()?.let {
            count--
             linkedList.removeHead()
        } ?: throw UnderflowError()
    }

    override fun isEmpty() = count ==0

    override fun enqueue(data: T) {
        if (count >= capacity){
            throw Exception("Queue has reached it's limit")
        }
        count++
        linkedList.append(data)
    }

    override fun toString(): String {
        val sb = StringBuffer()
        var _count = 0
        linkedList.forEach{
            _count += 1
            sb.append(it)
            sb.append(",")
        }
        return "[$sb]"
    }

}