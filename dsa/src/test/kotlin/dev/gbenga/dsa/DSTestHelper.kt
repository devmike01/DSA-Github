package dev.gbenga.dsa

import dev.gbenga.dsa.collections.Queue
import dev.gbenga.dsa.collections.Stack
import dev.gbenga.dsa.collections.list.LinkedList
import kotlin.test.assertTrue

inline fun <reified T> areTheSame(actual: Stack<T>, expected: Stack<T>){
    var areTheSame = actual.size() == expected.size()
    if (areTheSame){
        while (!actual.isEmpty() && !expected.isEmpty()){
            if (actual.pop() != expected.pop()){
                areTheSame = false
                break
            }
        }
    }
    assertTrue(areTheSame)
}


//inline fun <reified T> areTheSame(actual: Queue<T>, expected: Queue<T>){
//    var areTheSame = actual.size() != expected.size()
//    if (areTheSame){
//        while (!actual.isEmpty() && !expected.isEmpty()){
//            if (actual.pop() != expected.pop()){
//                areTheSame = false
//                break
//            }
//        }
//    }
//    assertTrue(areTheSame)
//}