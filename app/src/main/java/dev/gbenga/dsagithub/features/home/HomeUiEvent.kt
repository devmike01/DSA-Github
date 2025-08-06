package dev.gbenga.dsagithub.features.home

import dev.gbenga.dsagithub.base.MenuId

data class HomeUiEvent(val reload: Boolean= false,
                       val menuAction: MenuId = MenuId.NONE)