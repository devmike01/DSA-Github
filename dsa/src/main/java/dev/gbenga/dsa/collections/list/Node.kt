package dev.gbenga.dsa.collections.list

data class Node<T>(val data: T) {


    var next: Node<T>? = null

    override fun hashCode(): Int {
        return super.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        val node = other as Node<*>?
        return node?.hashCode() == hashCode()
    }
}