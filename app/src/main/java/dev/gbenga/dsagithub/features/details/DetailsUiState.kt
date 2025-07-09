package dev.gbenga.dsagithub.features.details

import dev.gbenga.dsa.collections.list.LinkedList
import dev.gbenga.dsagithub.base.UiState

class DetailsUiState(content: UiState<TabContent> = UiState.Loading())

data class TabContent(val tab: String, val content: LinkedList<Any>)