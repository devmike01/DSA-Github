package dev.gbenga.dsa.collections

import java.io.Serializable

interface Collections<T>  : Serializable {

    fun isEmpty(): Boolean

    fun isNotEmpty(): Boolean

    @Deprecated("To be removed", replaceWith = ReplaceWith("individual data structure's linearSearch"))
    fun linearSearch(predicate: (T) -> Boolean): T?

    fun remove(predicate: (T?) -> Boolean): Boolean
}