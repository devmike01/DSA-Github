package dev.gbenga.dsa.ext

import dev.gbenga.dsa.collections.list.Node

fun <T> StringBuffer.chain(value: Node<T>?){
    append(value)
    append(" -> ")
}

fun <T> StringBuffer.string(): String{
    return "[${toString()}]"
}

fun StringBuffer.llTail(){
    append("null")
}