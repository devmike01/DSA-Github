package dev.gbenga.dsagithub.base

import dev.gbenga.dsagithub.R


data class MenuItem (val icon: MenuIcon = MenuIcon.NONE,
                     val id: MenuId = MenuId.NONE)

enum class MenuIcon {
    REVERSE, SORT, SWAP, NONE
}

enum class MenuId{
    REVERSE, SORT, SWAP, NONE
}

fun MenuIcon.useIcon(): Int{
    return when (this) {
        MenuIcon.SORT -> android.R.drawable.ic_menu_sort_alphabetically
        MenuIcon.SWAP -> android.R.drawable.ic_menu_set_as
        MenuIcon.REVERSE -> android.R.drawable.ic_menu_revert
        else -> -1
    }
}
