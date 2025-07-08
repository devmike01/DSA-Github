package dev.gbenga.dsa.collections

import java.io.Serializable

interface SerializedDS<T>: Serializable {
    fun toList(): List<T>
}