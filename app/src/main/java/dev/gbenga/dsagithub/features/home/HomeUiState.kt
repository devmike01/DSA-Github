package dev.gbenga.dsagithub.features.home

import dev.gbenga.dsa.collections.list.LinkedList
import dev.gbenga.dsa.collections.list.LinkedListImpl
import dev.gbenga.dsagithub.base.UiState
import dev.gbenga.dsagithub.data.database.Favourite
import dev.gbenga.dsagithub.features.home.data.User

data class HomeUiState(val users: UiState<LinkedList<User>> = UiState.Loading())


data class FavUsersUiState(val favUsers: LinkedList<Favourite> = LinkedListImpl())