package dev.gbenga.dsa

import dev.gbenga.dsa.collections.list.Node
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import kotlin.test.assertEquals

@RunWith(JUnit4::class)
class NodeTest {

    @Test
    fun testNodeEquality(){
        val nextNode = Node(23)
        var node = Node(data = 43)
        node = node.copy(next = nextNode)
        assertEquals(node.next, nextNode)
    }
}