package dev.gbenga.dsagithub.features.details

import dev.gbenga.dsa.collections.list.LinkedList
import dev.gbenga.dsagithub.base.AppViewModel
import dev.gbenga.dsagithub.base.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class DetailViewModel : AppViewModel() {

    private val _userContent = MutableStateFlow<DetailsUiState>(DetailsUiState())
    val userContent : StateFlow<DetailsUiState> = _userContent


    fun populateDetailsTabContent(){
//        val linkedList = LinkedList<TabContent>()
//        _userContent.update { it.copy(content = UiState.) }
    }
}