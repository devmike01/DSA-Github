package dev.gbenga.dsagithub.features.details

import dev.gbenga.dsa.collections.list.LinkedList
import dev.gbenga.dsa.collections.list.LinkedListImpl
import dev.gbenga.dsagithub.base.UiState

data class DetailsUiState(
    val userRepos: UiState<LinkedList<UserRepositories>> = UiState.Loading(),
    val unFavourite: UiState<String> = UiState.Idle()
    )

enum class MessengerAction{
    CLOSE_SCREEN, NOTHING
}
data class DetailMessenger(val message: String="",
                           val action: MessengerAction = MessengerAction.NOTHING)

data class TabContent(val tab: String="",
                      val content: LinkedList<TabContent> = LinkedListImpl()
)