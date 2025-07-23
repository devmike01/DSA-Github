package dev.gbenga.dsa

import dev.gbenga.dsa.collections.list.LinkedList
import dev.gbenga.dsa.collections.list.LinkedListImpl
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import kotlin.test.assertEquals
import kotlin.test.assertNull

@RunWith(JUnit4::class)
class LinkedListTest {

    @Test
    fun testAppend(){
        val linkedList = LinkedList<String>()
        linkedList.append("Banana")
        linkedList.append("Mango")
        linkedList.append("Orange")
        linkedList.append("Watermelon")
       // assertEquals(linkedList.size(), 4)
        assertEquals(linkedList.lastOrNull(), "Watermelon")
        linkedList.clear()
        assertNull(linkedList.lastOrNull())
    }

    @Test
    fun testPrepend(){
        val linkedList = LinkedListImpl<String>()
        linkedList.prepend("Banana")
        linkedList.prepend("Mango")
        linkedList.prepend("Orange")
        assertEquals(linkedList.size(), 3)
        assertEquals(linkedList.peekHead(), "Orange")
        linkedList.clear()
        assertNull(linkedList.peekHead())
    }

    @Test
    fun textNodeRemoval(){
        val linkedList = LinkedList<String>()
        linkedList.append("John")
        linkedList.append("Judas")
        linkedList.append("James")
        linkedList.append("Judea")
        linkedList.remove("Judea")
    }

    @Test
    fun testReverse(){
        val linkedList = LinkedListImpl<Int>()
        linkedList.append(2)
        linkedList.append(10)
        linkedList.append(1)
        linkedList.append(3)
        linkedList.append(0)
        linkedList.swap(2, 1)
        linkedList.reverse()
        //
    }

    @Test
    fun testInsertionSort(){
        val linkedList = LinkedList<Int>()
        linkedList.append(2)
        linkedList.append(10)
        linkedList.append(1)
        linkedList.append(3)

    }



    @Test
    fun sampleBubbleSort(){
        val list = mutableListOf<Int>()
        list.add(2)
        list.add(32)
        list.add(22)
        list.add(0)
        list.add(3)
        list.add(1)

        for (i in 0 until list.size){
            for (x in 0 until list.size){
                if (list[i] < list[x]){
                    val temp = list[i]
                    list[i] = list[x]
                    list[x] = temp
                }
            }
        }
    }


    @Test
    fun sampleInsertionSort(){
        val list = mutableListOf<Int>()
        listOf<Int>(1, 0, 4, 2).forEach {
            list.add(it)
        }

        for (i in 0 until list.size){
            for (x in 0 until i){
                if (list[i] < list[x]){
                    val temp = list[i]
                    list[i] = list[x]
                    list[x] = temp
                }
            }
        }
    }
}