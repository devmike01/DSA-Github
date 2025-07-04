package dev.gbenga.dsa.ext

fun <T> StringBuffer.chain(value: T){
    append(value)
    append(" -> ")
}

fun <T> StringBuffer.string(): String{
    return "[${toString()}]"
}

fun StringBuffer.llTail(){
    append("null")
}