package dev.gbenga.dsa.collections.list

import dev.gbenga.dsa.collections.Collections

interface LinkedList<T> : Collections {

    fun peekHead(): T?
    fun removeHead(): T?
    fun append(value: T)
    fun lastOrNull(): T?
    fun remove(predicate: (T?) -> Boolean): Boolean
    fun prepend(value: T)
    fun insertionSort()
    fun linearSearch(predicate: (T) -> Boolean): T?
    fun swap(x: T, y: T)
    fun forEach(block: (T) -> Unit)
    fun bubbleSort(predicate: (T) -> Boolean): T?
    fun size(): Int
    fun reverse()
    fun clear()
}

class LinkedListImpl<T> : LinkedList<T> {

    private var head: Node<T>? = null

    private var _size =0

    override fun peekHead(): T? = head?.data

    override fun size(): Int = _size

    override fun removeHead(): T?{
        val removed = head
        head = head?.next
        return removed?.data?.also {
            _size --
        }
    }

    override fun append(value: T){
        _size += 1
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


    override fun lastOrNull(): T? {
        var cur = head
        while (cur?.next != null){
            cur = cur.next
        }
        return cur?.data
    }

    private fun decrementSize(){
        if (_size > 0){
            _size--
        }
    }

    override fun remove(predicate: (T?) -> Boolean): Boolean{
        if (predicate(head?.data)){
            head = head?.next
            decrementSize()
            return true
        }

        var current = head
        while (current?.next != null){
            if (predicate(current.next?.data)){
                current.next = current.next?.next
                decrementSize()
                return true
            }
            current = current.next
        }
        return false
    }

    override fun prepend(value: T){
        // 1 -> 2 -> 0 -> null
        _size += 1
        val newNode = Node(value) // 4
        newNode.next = head
        head = newNode
    }

    override fun forEach(block: (T) -> Unit){
        var curNode: Node<T>? = head
        while (curNode != null){
            block(curNode.data)
            curNode = curNode.next
        }
    }


    override fun reverse(){
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

//    fun <R> filter(predicate: (T) -> T): T{
//
//    }

    override fun bubbleSort(data: (T) -> Boolean): T?{
        TODO("Not yet implemented")
    }

    override fun insertionSort(){
        TODO("Not yet implemented")
    }


    // Takes O(n)
    override fun linearSearch(predicate: (T) -> Boolean): T?{
        var curr = head
        // Negate to search the linkedlist until we can find the item
        while (curr != null && !predicate.invoke(curr.data)){
            curr = curr.next
        }
        return curr?.data
    }

    override fun swap(x: T, y: T){
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

    override fun clear(){
        head = null
        _size =0
    }

    override fun toString(): String = buildString {
        var curNode: Node<T>? = head
        while (curNode != null){
            append(curNode.data)
            if (curNode.next != null){
                append("->")
            }
            curNode = curNode.next
        }
    }

    override fun isEmpty(): Boolean {
        return _size ==0
    }

    override fun isNotEmpty(): Boolean {
        return !isEmpty()
    }
}




inline fun <reified T> LinkedList<T>.toArray(): Array<T?>{
    return this.peekHead()?.let { headData ->
        val array = arrayOfNulls<T>(size())
        var index = -1

        var curNode: Node<T>? = Node(headData)
        while (curNode != null && index < size()){
            index += 1
            array[index] = curNode.data
            curNode = curNode.next
        }
        array
    } ?: emptyArray()

}