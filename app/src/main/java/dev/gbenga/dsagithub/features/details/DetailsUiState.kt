package dev.gbenga.dsagithub.features.details

import dev.gbenga.dsa.collections.list.LinkedList
import dev.gbenga.dsa.collections.list.LinkedListImpl
import dev.gbenga.dsagithub.base.UiState

data class DetailsUiState(
    val userRepos: UiState<LinkedList<UserRepositories>> = UiState.Loading())

data class TabContent(val tab: String="",
                      val content: LinkedList<TabContent> = LinkedListImpl()
)