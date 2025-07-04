package dev.gbenga.dsa

import dev.gbenga.dsa.collections.list.LinkedList
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.lang.System.Logger
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
        assertEquals(linkedList.lastOrNull(), "Orange")
        linkedList.clear()
        assertNull(linkedList.lastOrNull())
    }

    @Test
    fun testPrepend(){
        val linkedList = LinkedList<String>()
        linkedList.prepend("Banana")
        linkedList.prepend("Mango")
        linkedList.prepend("Orange")
        assertEquals(linkedList.firstOrNull(), "Orange")
        linkedList.clear()
        assertNull(linkedList.firstOrNull())
    }

}