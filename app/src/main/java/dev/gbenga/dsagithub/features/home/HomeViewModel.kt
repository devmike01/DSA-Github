package dev.gbenga.dsagithub.features.home

import android.util.Log
import androidx.lifecycle.viewModelScope
import dev.gbenga.dsa.collections.list.LinkedList
import dev.gbenga.dsa.collections.list.LinkedListImpl
import dev.gbenga.dsagithub.base.AppViewModel
import dev.gbenga.dsagithub.base.MenuIcon
import dev.gbenga.dsagithub.base.MenuId
import dev.gbenga.dsagithub.base.MenuItem
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

    private val _menus = MutableStateFlow<LinkedList<MenuItem>>(LinkedListImpl<MenuItem>())
    val menus : StateFlow<LinkedList<MenuItem>> = _menus

    init {
        viewModelScope.launch {
            _homeUiState.update { it.copy(users = UiState.Loading()) }
            homeRepository.getUsers().let { usersState ->
                if(usersState.isSuccess){
                    _homeUiState.update {
                        it.copy(users =  UiState.Success(usersState.getOrDefault(LinkedListImpl())))
                    }
                }else{
                    _homeUiState.update {
                        it.copy(users =  UiState.Error(usersState.exceptionOrNull()?.message ?: ""))
                    }
                }
            }
        }
    }

    fun loadMenus(){
        _menus.update {
            LinkedListImpl<MenuItem>().apply {
                prepend(MenuItem(icon = MenuIcon.REVERSE))
                prepend(MenuItem(icon = MenuIcon.SWAP))
                prepend(MenuItem(icon = MenuIcon.SORT))
            }
        }
    }

    fun setOnMenuClick(id: MenuId){
        Log.d("MenuItem", "--> MenuItem: $id")
    }
}