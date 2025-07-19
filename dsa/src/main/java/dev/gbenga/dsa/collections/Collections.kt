package dev.gbenga.dsa.collections

import java.io.Serializable

interface Collections<T>  : Serializable {

    fun isEmpty(): Boolean

    fun isNotEmpty(): Boolean

    fun bubbleSort(predicate: (T) -> Boolean): T?

    fun linearSearch(predicate: (T) -> Boolean): T?

    fun remove(predicate: (T?) -> Boolean): Boolean
}