package dev.gbenga.dsa

import dev.gbenga.dsa.collections.growBinarySearchTree
import dev.gbenga.dsa.collections.list.linkedListOf
import junit.framework.TestCase.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import kotlin.test.assertEquals

@RunWith(JUnit4::class)
class BSTTest {

    @Test
    fun testBST(){
        val bst = growBinarySearchTree<Any>(6,5,7,2,5,8)
        assertEquals(bst.search(12), null) // Check for can't find value
        assertEquals(bst.search(2), 2)
        assertEquals(bst.searchRecursion(5, bst.root), 5)
        assertTrue(bst.inOrderPrint(bst.root) == linkedListOf(2,5,5,6,7,8))
        assertTrue(bst.preOrderPrint(bst.root) == linkedListOf(6,5,2,5,7,8))
        assertTrue(bst.postOrderPrint(bst.root) == linkedListOf(5,2,5,8,7,6))
    }

    @Test
    fun testMinMax(){
        val bst = growBinarySearchTree<Any>("Mango", "Orange", "PawPaw", "Apple")

        println("values: ${bst.preOrderPrint(bst.root)}")
        bst.rotateLeft()
        println("values: ${bst.preOrderPrint(bst.root)}")
        assertEquals(bst.findMin(bst.root), "Apple")
        assertEquals(bst.findMax(bst.root), "PawPaw")
    }
}