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

}