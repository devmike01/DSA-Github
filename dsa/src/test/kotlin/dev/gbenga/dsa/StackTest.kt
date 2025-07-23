package dev.gbenga.dsa

import dev.gbenga.dsa.collections.Stack
import dev.gbenga.dsa.collections.StackImpl
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import kotlin.test.assertEquals

@RunWith(JUnit4::class)
class StackTest {

    @Test
    fun testPop(){
        val stack: Stack<String> = StackImpl(5)
        stack.push("Cherries")
        stack.push("Mangoes")
        stack.push("Orange")
        stack.pop()
        areTheSame(stack, arrayOf("Cherries", "Mangoes").let { arr ->
            val stack: Stack<String> = StackImpl(5)
            arr.forEach { stack.push(it) }
            stack
        }.also { println(it.toString()) })
    }

}