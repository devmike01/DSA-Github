package dev.gbenga.dsa.collections.list

import dev.gbenga.dsa.collections.Collections


inline fun <reified T> linkedListOf(vararg values: T): LinkedList<T>{
    val linkedList : LinkedList<T> = LinkedListImpl()
    values.forEach {
        linkedList.append(it)
    }
    return linkedList
}

interface LinkedList<T> : Collections<T> {

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
    fun bubbleSort(): Node<T>?
    fun linearSearch(): Node<T>?
    fun <R> map(onMap: (T) -> R): LinkedList<R>
    fun clear()
}

class LinkedListImpl<T> : LinkedList<T> {

    private var head: Node<T>? = null

    private var _size =0
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

    override fun bubbleSort(): Node<T>?{
        if (head ==null || null == head?.next) return head
        var swapped: Boolean = false

        when(head?.data){
            is Int -> {
                do {
                    var intCurrent = head as? Node<Int>
                    swapped = false
                    while (intCurrent?.next != null){
                        if (intCurrent.data > intCurrent.next!!.data){
                            val temp = intCurrent.data
                            intCurrent.data = intCurrent.next!!.data
                            intCurrent.next?.data = temp
                            swapped = true
                        }
                        intCurrent = intCurrent.next
                    }
                }while (swapped)

            }
            is String -> {
                do {
                    var strCurrent = head as? Node<String>
                    swapped = false
                    while (strCurrent?.next != null){
                        if (strCurrent.data.lowercase() > strCurrent.next!!.data.lowercase()){
                            val temp = strCurrent.data
                            strCurrent.data = strCurrent.next!!.data
                            strCurrent.next!!.data = temp
                            swapped = true
                        }
                        strCurrent = strCurrent.next
                    }
                }while (swapped)
            }
            else -> {
                throw UnsupportedOperationException("Type ${head?.data} is not supported. Supported types: {str, int}")
            }
        }
        return head
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

    override fun linearSearch(): Node<T>? {
        TODO("Not yet implemented")
    }

    override fun <R> map(transform: (T) -> R): LinkedList<R> {
        val linkedList = LinkedListImpl<R>()
        var curr : Node<T>? = head
        while (curr != null){
            linkedList.append(transform(curr.data))
            curr = curr.next
        }
        return linkedList
    }


    override fun bubbleSort(predicate: (Node<T>) -> Boolean): Node<T>?{
        return bubbleSortWithPrediction(predicate)
    }

    override fun insertionSort(){
      //  insertionSortString<T>()
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