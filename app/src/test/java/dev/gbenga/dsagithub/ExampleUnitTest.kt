package dev.gbenga.dsagithub

import dev.gbenga.dsagithub.nav.choir.Choir
import org.junit.Test

import org.junit.Assert.*
import kotlin.reflect.full.declaredFunctions
import kotlin.reflect.full.declaredMembers
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        Choir().putRoute<Jumping> { mango() }
        assertEquals(4, 2 + 2)
    }

    fun mango(): String{ return ""}
}

data class Jumping(val james: String="Hello", val john: String="Hellllo")