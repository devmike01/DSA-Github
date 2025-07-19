package dev.gbenga.dsagithub.base

fun String.initial(): String{
    return this[0].uppercase()
}

fun String.titleCase(): String {
    if(this.length < 2) return ""
    return this.let { "${it.initial()}${it.substring(1).lowercase()}"}
}