package dev.gbenga.dsa


infix fun <T: Comparable<T>> Comparable<T>.greater(value: T): Boolean?{
    return this > value
}

fun String.startWithIgnoreCase(value: String): Boolean{
    return this.lowercase().startsWith(value.lowercase())
}