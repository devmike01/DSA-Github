package dev.gbenga.dsa

import dev.gbenga.dsa.collections.list.LinkedList
import dev.gbenga.dsa.collections.list.LinkedListImpl
import dev.gbenga.dsa.collections.list.Node
import dev.gbenga.dsa.collections.list.linkedListOf
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.lang.UnsupportedOperationException
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
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
        assertTrue(linkedList == linkedListOf("John","Judas", "James", "Judea"))
        linkedList.remove { it == "Judea" }
        assertTrue(linkedList == linkedListOf("John","Judas", "James"))
    }

    @Test
    fun testReverse(){
        val linkedList = linkedListOf(2,10,1,3,0)
        assertTrue(linkedList == linkedListOf(2,10,1,3,0))
        linkedList.reverse()
        assertTrue(linkedList == linkedListOf(0,3,1,10,2))
    }

//    @Test
//    fun testInsertionSort(){
//        val linkedList = LinkedListImpl<Int>()
//        linkedList.append(2)
//        linkedList.append(10)
//        linkedList.append(1)
//        linkedList.append(3)
//        // Todo: implement insertion sort later to test
//    }
//
//

    @Test(expected = UnsupportedOperationException::class)
    fun testSortUnSupported(){
        val unSupportedTypes = linkedListOf(Any(), Any())
        unSupportedTypes.bubbleSort()
    }

    @Test
    fun testBubbleSort(){
        val intList = linkedListOf(2,1,5,0,10, 3, -1)
        intList.bubbleSort()
        val strList = linkedListOf("Mango", "Orange", "Apple", "Imbu")
        strList.bubbleSort()
        assertTrue(intList == linkedListOf(-1, 0,1,2,3,5,10))
        assertTrue(strList == linkedListOf("Apple", "Imbu","Mango", "Orange"))
    }

    @Test
    fun testLinearSearch(){
        val intList = linkedListOf(2,1,5,0,10, 3, -1)
        val strList = linkedListOf("A", "B", "C")
        assertEquals(intList.linearSearch(4)?.data, null)
        assertEquals(intList.linearSearch(-1)?.data, -1)
        assertEquals(strList.linearSearch("A")?.data, "A")
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