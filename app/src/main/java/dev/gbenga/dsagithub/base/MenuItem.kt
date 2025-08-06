package dev.gbenga.dsagithub.base

import dev.gbenga.dsagithub.R


data class MenuItem (val icon: MenuIcon = MenuIcon.NONE,
                     val id: MenuId = MenuId.NONE,
    val hide: Boolean = false): Comparable<MenuItem> {
    override fun compareTo(other: MenuItem): Int {
        return this.id.compareTo(other.id)
    }
}

enum class MenuIcon {
    REVERSE, SORT, SWAP, SEARCH, NONE
}

enum class MenuId{
    REVERSE, SORT, SWAP, SEARCH, NONE, RESET
}

fun MenuIcon.useIcon(): Int{
    return when (this) {
        MenuIcon.SORT -> android.R.drawable.ic_menu_sort_alphabetically
        MenuIcon.SWAP -> android.R.drawable.ic_menu_set_as
        MenuIcon.REVERSE -> android.R.drawable.ic_menu_revert
        MenuIcon.SEARCH -> android.R.drawable.ic_menu_search
        else -> -1
    }
}
