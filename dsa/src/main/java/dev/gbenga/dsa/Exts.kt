package dev.gbenga.dsa


infix fun <T: Comparable<T>> Comparable<T>.greater(value: T): Boolean?{
    return this > value
}