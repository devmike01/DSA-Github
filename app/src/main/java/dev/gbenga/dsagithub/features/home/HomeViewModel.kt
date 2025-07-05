package dev.gbenga.dsagithub.features.home

import androidx.lifecycle.viewModelScope
import dev.gbenga.dsa.collections.list.LinkedList
import dev.gbenga.dsagithub.base.AppViewModel
import dev.gbenga.dsagithub.base.UiState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(private val homeRepository: HomeRepository) : AppViewModel() {

    private val _action = MutableSharedFlow<HomeUiEvent>()
    val action : SharedFlow<HomeUiEvent> = _action.asSharedFlow()

    private val _homeUiState = MutableStateFlow(HomeUiState())
    val homeUiState : StateFlow<HomeUiState> = _homeUiState.asStateFlow()

    init {
        viewModelScope.launch {
            _homeUiState.update { it.copy(users = UiState.Loading()) }
            homeRepository.getUsers().let { usersState ->
                if(usersState.isSuccess){
                    _homeUiState.update {
                        it.copy(users =  UiState.Success(usersState.getOrDefault(LinkedList())))
                    }
                }else{
                    _homeUiState.update {
                        it.copy(users =  UiState.Error(usersState.exceptionOrNull()?.message ?: ""))
                    }
                }
            }
        }
    }
}