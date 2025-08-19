package dev.gbenga.dsa.collections.list

import dev.gbenga.dsa.collections.Collections
import java.io.Serializable

 fun <T> linkedListOf(vararg values: T): LinkedList<T>{
    val linkedList : LinkedList<T> = LinkedListImpl()
    values.forEach {
        linkedList.append(it)
    }
    return linkedList
}

 fun <T> emptyLinkedList(): LinkedList<T> = EmptyLinkedList as LinkedList<T>


internal object EmptyLinkedList : LinkedList<Nothing>, Serializable {
    override fun join(values: LinkedList<Nothing>): LinkedList<Nothing> = LinkedListImpl<Nothing>()

    override fun plus(values: LinkedList<Nothing>): LinkedList<Nothing> = LinkedListImpl<Nothing>()

    override fun peekTailOrNull(): Node<Nothing>? = null

    override fun peekHeadNode(): Node<Nothing>? = null

    override fun peekHead(): Nothing? =null

    override fun removeHead(): Nothing? = null

    override fun append(value: Nothing) { }

    override fun lastOrNull(): Nothing? = null

    override fun prepend(value: Nothing)  = Unit

    override fun insertionSort() = Unit

    override fun swap(x: Nothing?, y: Nothing?) = Unit

    override fun forEach(block: (Nothing) -> Unit) = Unit

    override fun size(): Int =0

    override fun reverse() = Unit

    override fun bubbleSort(predicate: (Node<Nothing>) -> Boolean): Node<Nothing>? = null

    override fun linearSearch(query: Nothing): Node<Comparable<Nothing>>? = null

    override fun <R> map(onMap: (Nothing) -> R): LinkedList<R> = LinkedListImpl()

    override fun clone(): LinkedList<Nothing> = LinkedListImpl()

    override fun clear() {
    }

    override fun isEmpty(): Boolean = !isNotEmpty()

    override fun isNotEmpty(): Boolean = false

    override fun linearSearch(predicate: (Nothing) -> Boolean): Nothing? = null

    override fun remove(predicate: (Nothing?) -> Boolean): Boolean = false

}

interface LinkedList<T> : Collections<T> {
    fun join(values: LinkedList<T>): LinkedList<T>
    operator fun plus(values: LinkedList<T>): LinkedList<T>
    fun peekTailOrNull(): Node<T>?
    fun peekHeadNode(): Node<T>?
    fun peekHead(): T?
    fun removeHead(): T?
    fun append(value: T)
    fun lastOrNull(): T?
    fun prepend(value: T)
    fun insertionSort()
    fun swap(x: T?, y: T?)
    fun forEach(block: (T) -> Unit)
    fun size(): Int
    fun reverse()
    fun bubbleSort(predicate: (Node<T>) -> Boolean) : Node<T>?
    //fun <T: Comparable<T>> bubbleSort(): Node<T>?
    fun linearSearch(query: T): Node<Comparable<T>>?
    fun <R> map(onMap: (T) -> R): LinkedList<R>
    fun clone(): LinkedList<T>
    fun clear()
}

class LinkedListImpl<T> : LinkedList<T> {

    private var head: Node<T>? = null
    private var tail: Node<T>? = null

    private var _size =0

    override fun plus(values: LinkedList<T>): LinkedList<T> {
        val newList : LinkedList<T> = LinkedListImpl<T>()
        var current= peekHeadNode()
        var hasJoined = false
        while (current != null){
            newList.append(current.data)
            current = current.next
        }

        var current2 = values.peekHeadNode()
        while (current2 != null){
            newList.append(current2.data)
            current2 = current2.next
        }
        return newList
    }

    override fun join(values: LinkedList<T>): LinkedList<T> {
        val newList : LinkedList<T> = LinkedListImpl<T>()
        var current= peekHeadNode()
        var hasJoined = false
        while (current != null){
            if (current.next == null && !hasJoined){
                current.next = values.peekHeadNode()
                hasJoined = true
            }
            newList.append(current.data)
            current = current.next
        }
        return newList
    }


    override fun peekTailOrNull(): Node<T>? = tail

    override fun peekHeadNode(): Node<T>? {
        return head
    }

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
            tail = newNode
            return
        }
        tail?.next = newNode
        tail = newNode
    }


    override fun lastOrNull(): T? = tail?.data

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



    internal fun bubbleSortWithPrediction(prediction: (node: Node<T>) -> Boolean): Node<T>?{
        if (head == null || head?.next == null)return null

        var swapped: Boolean // = false
        do {
            var cur: Node<T>? = head
            swapped = false
            while (cur?.next != null){
                if (prediction(cur)){
                    val temp = cur.data
                    cur.data = cur.next!!.data
                    cur.next?.data = temp!!
                    swapped = true
                }
                cur = cur.next
            }
        }while (swapped)
        return head
    }

    override fun linearSearch(query: T) : Node<Comparable<T>>?{
        try {
            var current : Node<Comparable<T>>? = head  as? Node<Comparable<T>>
            while (current != null && query != current.data){
                current = current.next
            }
            //query: T
            return current
        }catch (e: ClassCastException){
            throw UnsupportedOperationException("$query is not a supported type")
        }
    }

    override fun <R> map(transform: (T) -> R): LinkedList<R> {
        val result = LinkedListImpl<R>()
        var curr : Node<T>? = head
        while (curr != null){
            result.append(transform(curr.data))
            curr = curr.next
        }
        return result
    }

    override fun clone(): LinkedList<T> {
        val result = LinkedListImpl<T>()
        var curr : Node<T>? = head
        while (curr != null){
            result.append(curr.data)
            curr = curr.next
        }
        return result
    }


    override fun bubbleSort(predicate: (Node<T>) -> Boolean): Node<T>?{
        return bubbleSortWithPrediction(predicate)
    }

    override fun insertionSort(){

    }

    private fun <T: Comparable<T>> insertionSortString(): Node<T>?{
        var sorted: Node<T>? = null
        var current : Node<T>? = head as? Node<T>
        while (current != null){
            val next = current.next
            if (sorted == null || current.data < sorted.data){
                current.next = sorted
                sorted = current
            }else{
                var temp = sorted
                while (temp?.next != null && temp.next!!.data < current.data){
                    temp = temp.next
                }
                current.next = temp?.next
                temp?.next = current
            }
            current = next
        }
        return sorted
    }

    private fun insertionSortInt(): Node<Int>?{
        var sorted: Node<Int>? = null
        var current: Node<Int>? = head as? Node<Int>
        while (current != null){
            val next = current.next

            if (sorted == null || current.data < sorted.data){
                current.next = sorted
                sorted = current
            }else{
                var temp = sorted
                while (temp?.next != null && temp.next!!.data < current.data){
                    temp = temp.next
                }
                current.next = temp?.next
                temp?.next = current
            }
            current = next
        }
        return sorted
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

    override fun swap(x: T?, y: T?){
        if (y == null || null == x )return
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

    override fun equals(other: Any?): Boolean {
        if(this === other)return true
        if (other === null || this !is LinkedList<*>)return false
        val otherList : LinkedList<*> = other as LinkedList<*>
        if (this._size != otherList.size()){
            return false
        }
        var current = this.head
        var otherCurrent = otherList.peekHeadNode()
        while (current != null && otherCurrent != null){
            if (otherCurrent.data?.equals(current.data) == false){
                return false
            }
            otherCurrent = otherCurrent.next
            current = current.next
        }
        return current == null && otherCurrent == null
    }

    override fun hashCode(): Int {
        var result = 1
        var current : Node<*>? = head
        return super.hashCode()
    }
}


fun <T> LinkedList<T>.linearFind(test: (T) -> Boolean): T?{
    var current : Node<T>? = peekHeadNode()
    while (current != null){
        if (test(current.data)){
            return current.data
        }
        current = current.next
    }
    return null
}

fun <T> LinkedList<T>.linearFilter(test: (T) -> Boolean): LinkedList<T>{
    var result: LinkedList<T> = LinkedListImpl()
    var current : Node<T>? = peekHeadNode()
    while (current != null){
        if (test(current.data)){
            result.append(current.data)
        }
        current = current.next
    }

    return result
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

fun <T> Array<T>.toMyLinkedList(): LinkedList<T>{
    val linkedList = LinkedListImpl<T>()
    forEach {
        linkedList.append(it)
    }
    return linkedList
}

fun <T: Comparable<T>>  LinkedList<T>.bubbleSorted(): Node<T>?{
    val head = peekHeadNode()
    if (head ==null || null == head.next) return head
    var swapped: Boolean = false

    do {
        var current = head as? Node<Comparable<T>>
        swapped = false
        while (current?.next != null){
            if (current.data > (current.next!!.data as T)){
                val temp = current.data
                current.data = current.next!!.data
                current.next?.data = temp
                swapped = true
            }
            current = current.next
        }
    }while (swapped)
    return head as Node<T>?
}