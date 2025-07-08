package dev.gbenga.dsa

import dev.gbenga.dsa.collections.list.LinkedList
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
        assertEquals(linkedList.size(), 4)
        assertEquals(linkedList.lastOrNull(), "Watermelon")
        println("popped: ${linkedList.pop()}")
        assertEquals(linkedList.size(), 3)
        println("linkedList -> ${linkedList.toString()}")
        linkedList.clear()
        assertNull(linkedList.lastOrNull())
    }

    @Test
    fun testPrepend(){
        val linkedList = LinkedList<String>()
        linkedList.prepend("Banana")
        linkedList.prepend("Mango")
        linkedList.prepend("Orange")
        println("linkedList -> ${linkedList.size()}")
        assertEquals(linkedList.size(), 3)
        assertEquals(linkedList.firstOrNull(), "Orange")
        linkedList.clear()
        assertNull(linkedList.firstOrNull())
    }

    @Test
    fun textNodeRemoval(){
        val linkedList = LinkedList<String>()
        linkedList.append("John")
        linkedList.append("Judas")
        linkedList.append("James")
        linkedList.append("Judea")
        println("linkedList -|> $linkedList")
        linkedList.remove("Judea")
        println("linkedList -|> $linkedList")
    }

    @Test
    fun testReverse(){
        val linkedList = LinkedList<Int>()
        linkedList.append(2)
        linkedList.append(10)
        linkedList.append(1)
        linkedList.append(3)
        linkedList.append(0)
        linkedList.swap(2, 1)
        println("original: $linkedList")
        println("original: ${linkedList.search(2)}")
        linkedList.reverse()
        println("original: $linkedList")
        //
    }
}