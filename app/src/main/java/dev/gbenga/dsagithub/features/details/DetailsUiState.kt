package dev.gbenga.dsagithub.features.details

import dev.gbenga.dsa.collections.list.LinkedList
import dev.gbenga.dsagithub.base.UiState

data class DetailsUiState(
    val content: UiState<LinkedList<TabContent>> = UiState.Loading())

data class TabContent(val tab: String="",
                      val content: LinkedList<TabContent> = LinkedList()
)