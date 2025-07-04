package dev.gbenga.dsa.collections.list

import dev.gbenga.dsa.ext.chain
import dev.gbenga.dsa.ext.llTail
import dev.gbenga.dsa.ext.string

class LinkedList<T> {

    private var head: Node<T>? = null;

    fun append(value: T){
        val newNode = Node(value)
        if (head  == null){
            head = newNode
            return
        }

        while (head?.next != null){
            head = head?.next
        }
        head = head?.copy(next = newNode)
    }


    fun firstOrNull(): T? = head?.data

    fun lastOrNull(): T? {
        var cur = head
        while (cur?.next != null){
            cur = cur.next
        }
        return cur?.data
    }

    fun prepend(value: T){
        var newNode = Node(value)
        newNode = newNode.copy(next = head)
        head = newNode
    }

    fun forEach(block: (T) -> Unit){
        var curNode: Node<T>? = head;
        while (curNode != null){
            block(curNode.data)
            curNode = curNode.next
        }
    }

    fun clear(){
        head = null;
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