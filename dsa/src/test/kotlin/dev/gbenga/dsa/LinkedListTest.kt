package dev.gbenga.dsa

import dev.gbenga.dsa.collections.list.LinkedList
import dev.gbenga.dsa.collections.list.LinkedListImpl
import dev.gbenga.dsa.collections.list.Node
import dev.gbenga.dsa.collections.list.linkedListOf
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

@RunWith(JUnit4::class)
class LinkedListTest {

    @Test
    fun testAppend(){
        val linkedList = LinkedListImpl<String>()
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
        val linkedList = LinkedListImpl<String>()
        linkedList.append("John")
        linkedList.append("Judas")
        linkedList.append("James")
        linkedList.append("Judea")
        linkedList.remove { it == "Judea" }
    }

    @Test
    fun testReverse(){
        val linkedList = linkedListOf(2,10,1,3,0)
        assertTrue(linkedList == linkedListOf(2,10,1,3,0))
        linkedList.reverse()
        assertTrue(linkedList == linkedListOf(0,3,1,10,2))
    }

    @Test
    fun testInsertionSort(){
        val linkedList = LinkedListImpl<Int>()
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
//        val ll = LinkedListImpl<Int>()
//        ll.append(21)
//        ll.append(1)
//        ll.append(2)
//        ll.append(19)
//        ll.append(0)
//        var cNode = ll.peekHeadNode()
//
    }
}