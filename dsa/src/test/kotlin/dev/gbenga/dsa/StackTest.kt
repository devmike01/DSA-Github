package dev.gbenga.dsa

import dev.gbenga.dsa.collections.Stack
import dev.gbenga.dsa.collections.StackImpl
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class StackTest {

    @Test
    fun testPop(){
        val stack: Stack<String> = StackImpl(5)
        stack.push("Mangoes")
        stack.push("Cherries")
        stack.push("Orange")
       // stack.pop()
        println("testPop: $stack")
    }

}