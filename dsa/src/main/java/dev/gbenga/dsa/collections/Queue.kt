package dev.gbenga.dsa.collections

import dev.gbenga.dsa.collections.list.LinkedList
import dev.gbenga.dsa.collections.list.Node


interface Queue<T> {

    fun dequeue(): T

    fun enqueue(data: T)

    fun isEmpty()

}

class QueueImpl<T> : Queue<T>{

    private val linkedList = LinkedList<T>()

    override fun dequeue(): T {
        val temp = linkedList.head?.data
        val newNode = linkedList.head?.next
        linkedList.head = newNode
        return temp ?: throw UnderflowError()
    }

    override fun isEmpty() {
        TODO("Not yet implemented")
    }

    override fun enqueue(data: T) {
        val head = linkedList.head
    }


}