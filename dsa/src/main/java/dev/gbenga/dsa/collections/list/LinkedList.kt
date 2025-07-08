package dev.gbenga.dsa.collections.list

import dev.gbenga.dsa.ext.chain
import dev.gbenga.dsa.ext.llTail
import dev.gbenga.dsa.ext.string

class LinkedList<T> {

    var head: Node<T>? = null;

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

    fun remove(value: T){
        // 4 -> 3 -> 1 -> 2 -> 0
        // rem 3
        // 4 -> 1 -> 2 -> 0
        var current = head
        while (current?.next != null && current.next?.data != value){
            current = current.next
        }
        current?.next = current?.next?.next?.also {
            _size -= 1
        }
    }

    fun pop(): T?{
        if(_size == 0)return null
        var current = head
        while (current?.next?.next != null){
            current = current.next
        }
        current?.next = null
        _size -= 1
        return current?.data
    }

    fun prepend(value: T){
        // 1 -> 2 -> 0 -> null
        _size += 1
        val newNode = Node(value) // 4
        newNode.next = head // 4 -> 1 -> 2 -> 0 -> null
        head = newNode // head = 4 -> 1 -> 2 -> 0 -> null
    }

    fun forEach(block: (T) -> Unit){
        var curNode: Node<T>? = head;
        while (curNode != null){
            block(curNode.data)
            curNode = curNode.next
        }
    }


    fun reverse(){
        // 1 -> 2 -> 4 -> 0 -> null
        if (head ==null){
            return
        }
        var prev: Node<T>? = null
        var curNode : Node<T>? = head
        while (curNode != null){
            val temp = curNode.next
            curNode.next = prev
            prev = curNode
            curNode = temp
        }
        head = prev
    }

    fun search(data: T): T?{
        // 1 2 3 null; f = 2
        var curr = head
        while (curr != null && curr.data != data){
            curr = curr.next
        }
        return curr?.data
    }

//    fun insertionSort(){
//        var curr : Node<T>? = head
//        while (curr != null){
//
//            curr = curr.next
//        }
//    }

    fun swap(x: T, y: T){
        if (x == y)return

        var prevX : Node<T>? = null
        var curX : Node<T>? = head
        while (curX != null && curX.data != x){
            prevX = curX
            curX = curX.next
        }

        var prevY: Node<T>? = null
        var curY : Node<T>? = head
        while (curY != null && curY.data != y){
            prevY = curY
            curY = curY.next
        }

        if (curY == null || curX == null){
            return
        }

        if (prevY != null){
            prevY.next = curX
        }else{
            head = curX
        }

        if (prevX != null){
            prevX.next = curY
        }else{
            head = curY
        }

        val temp = curX.next
        curX.next = curY.next
        curY.next = temp

    }

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




@Suppress("UNCHECKED_CAST")
inline fun <reified T> LinkedList<T>.toArray(): Array<T?>{
    val array = arrayOfNulls<T>(size())
    var index = -1

    var curNode: Node<T>? = this.head;
    while (curNode != null && index < size()){
        index += 1
        array[index] = curNode.data
        curNode = curNode.next
    }
    return array
}