package dev.gbenga.dsa.collections.list

import java.io.Serializable

data class Node<T>(var data: T): Serializable {


    var next: Node<T>? = null

    override fun hashCode(): Int {
        return super.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        val node = other as Node<*>?
        return node?.hashCode() == hashCode()
    }
}