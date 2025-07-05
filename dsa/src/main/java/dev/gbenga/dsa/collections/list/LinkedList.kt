package dev.gbenga.dsa.collections.list

import dev.gbenga.dsa.ext.chain
import dev.gbenga.dsa.ext.llTail
import dev.gbenga.dsa.ext.string

class LinkedList<T> {

    private var head: Node<T>? = null;

    private var _size =0

    fun size(): Int = _size

    fun append(value: T){
        _size += 1
        println("______size: $_size")
        val newNode = Node(value)
        if (head  == null){
            head = newNode
            return
        }

        var current = head
        while (current?.next != null){
            current = current.next
        }
        current?.next = newNode
    }


    fun firstOrNull(): T? = head?.data

    fun lastOrNull(): T? {
        var cur = head
        while (cur?.next != null){
            cur = cur.next
        }
        return cur?.data
    }

    fun pop(): T?{
        if(_size == 0)return null
        var current = head
        while (current?.next?.next != null){
            println("MENA -> ${current.data}")
            current = current.next
        }
        current?.next = null
        _size -= 1
        println("_size_ $_size")
        return current?.data
    }

    fun prepend(value: T){
        _size += 1
        val newNode = Node(value)
        newNode.next = head
        head = newNode
    }

    fun forEach(block: (T) -> Unit){
        var curNode: Node<T>? = head;
        while (curNode != null){
            println("linkedList -|> ${curNode.data}")
            block(curNode.data)
            curNode = curNode.next
        }
    }

//    fun <R> map(block: (T) -> R): R{
//        var curNode: Node<T>? = head;
//        while (curNode != null){
//            curNode = curNode.next
//        }
//    }

    fun clear(){
        head = null;
        _size =0
    }

    override fun toString(): String {
        val sb = StringBuffer()
        var curNode: Node<T>? = head;
        while (curNode != null){
            sb.chain(curNode.data)
            curNode = curNode.next
        }
        sb.llTail()
        return sb.string<T>()
    }
}