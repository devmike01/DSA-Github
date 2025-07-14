package dev.gbenga.dsagithub

import dev.gbenga.dsagithub.nav.choir.RouteMap
import org.junit.Test

class RouteMapTest {

    @Test
    fun testPut(){
        val routeMap = RouteMap<String, Int>()
        routeMap.put("a", 1)
        routeMap.put("b", 3)
        routeMap.put("c", 221)
        routeMap.put("a", 229)
        println("map: $routeMap")
    }
}