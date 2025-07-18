package dev.gbenga.dsa.collections

import java.io.Serializable

interface Collections  : Serializable {

    fun isEmpty(): Boolean

    fun isNotEmpty(): Boolean
}