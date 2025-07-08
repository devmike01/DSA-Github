package dev.gbenga.dsa.collections

import dev.gbenga.dsa.collections.list.LinkedList


interface Stack<T>{
    fun pop(): T

    fun push(value: T)

    fun peek(): T

    fun isEmpty(): Boolean
}

class StackImpl<T>(private val capacity: Int) : Stack<T> {

    private val linkedList = LinkedList<T>()

    private var count: Int = 0

    private fun checkCapacity(block: () -> Unit){
        if (count < capacity){
            return block()
        }
        throw StackOverflowError("Stack is full")
    }


    override fun pop(): T {
        return linkedList.removeHead()?.also { count -= 1 }  ?: throw UnderflowError("Cannot pop from an empty stack")
    }

    override fun peek(): T  {
        return linkedList.peekHead() ?: throw EmptyStackException()
    }

    override fun isEmpty(): Boolean = count == 0

    override fun push(value: T) = checkCapacity {
        // A, B, C
        count += 1
        linkedList.prepend(value)
    }

    override fun toString(): String {
        val sb = StringBuffer()
        var _count = 0
        linkedList.forEach{
            _count += 1
            sb.append(it)
            if (_count < count){
                sb.append(",")
            }
        }
        return "[$sb]"
    }
}