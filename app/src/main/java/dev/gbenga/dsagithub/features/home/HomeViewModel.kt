package dev.gbenga.dsagithub.features.home

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dev.gbenga.dsa.collections.Queue
import dev.gbenga.dsa.collections.QueueImpl
import dev.gbenga.dsa.collections.list.LinkedList
import dev.gbenga.dsa.collections.list.LinkedListImpl
import dev.gbenga.dsagithub.base.AppViewModel
import dev.gbenga.dsagithub.base.MenuIcon
import dev.gbenga.dsagithub.base.MenuId
import dev.gbenga.dsagithub.base.MenuItem
import dev.gbenga.dsagithub.base.UiState
import dev.gbenga.dsagithub.data.database.Favourite
import dev.gbenga.dsagithub.features.home.data.User
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(private val favouriteRepository: FavouriteRepository,
                    private val savedState: SavedStateHandle) : AppViewModel() {

    private val _action = MutableSharedFlow<HomeUiEvent>()
    val action : SharedFlow<HomeUiEvent> = _action.asSharedFlow()

    private val _homeUiState = MutableStateFlow(HomeUiState())
    val homeUiState : StateFlow<HomeUiState> = _homeUiState.asStateFlow()

    private val _favUserUiState = MutableStateFlow(FavUsersUiState())
    val favUserUiState : StateFlow<FavUsersUiState> = _favUserUiState.asStateFlow()

    private val _menus = MutableStateFlow<LinkedList<MenuItem>>(LinkedListImpl<MenuItem>())
    val menus : StateFlow<LinkedList<MenuItem>> = _menus

    private val _userList : LinkedList<User> = LinkedListImpl()
    private var _endQueue: Queue<Int> = QueueImpl<Int>(ENDLESS_SCROLL_SIZE)

    companion object{
        const val ENDLESS_SCROLL_SIZE = 2
        const val REFRESH_SCREEN = "HomeViewModel.REFRESH_SCREEN"
    }

    init {
        println("REFRESH_SCREEN: ${savedState.get<Boolean>(REFRESH_SCREEN)}")
    }

    fun loadMoreGithubUsers(){
        viewModelScope.launch {
            //val last = _homeUiState.value.
            favouriteRepository.getUsers(_endQueue.dequeue().also {
                println("_endQueue: $it")
            }).collectResultForUi()
        }
    }

    fun loadGithubUsers(){
        if(savedState.get<Boolean>(REFRESH_SCREEN) == null){
            viewModelScope.launch {
                favouriteRepository.getUsers().collectResultForUi()
                savedState[REFRESH_SCREEN] = true
            }
        }

    }

    fun updateStack(value: Int){
        val temp : Queue<Int> = QueueImpl(ENDLESS_SCROLL_SIZE)
        // val isPresent = _endQueue.l
        var notFound: Boolean = true
        while (!_endQueue.isEmpty()){
            val dq = _endQueue.dequeue()
            temp.enqueue(dq)
            notFound = dq != value
        }

        if (notFound){
            temp.enqueue(value)
        }
        println("FOUND: $notFound")
        _endQueue = temp
    }

    fun Result<LinkedList<User>>.collectResultForUi(){
        val users = LinkedListImpl<User>()
        _homeUiState.update { it.copy(users = UiState.Loading()) }
        let { usersState ->
            if(usersState.isSuccess){
                usersState.getOrDefault(LinkedListImpl()).forEach { user ->
                    _userList.append(user)
                }
                _userList.forEach {
                    users.append(it)
                }

                usersState.getOrNull()?.lastOrNull()?.let {
                    updateStack(it.id)
                }

                _homeUiState.update {
                    it.copy(users =  UiState.Success(users))
                }


            }else{
                _homeUiState.update {
                    it.copy(users =  UiState.Error(usersState.exceptionOrNull()?.message ?: ""))
                }
            }
        }
    }


    fun loadMenus(){
        _menus.update {
            LinkedListImpl<MenuItem>().apply {
                prepend(MenuItem(icon = MenuIcon.REVERSE,
                    id = MenuId.REVERSE))
                prepend(MenuItem(icon = MenuIcon.SWAP,
                    id = MenuId.SWAP))
                prepend(MenuItem(icon = MenuIcon.SORT,
                    id = MenuId.SORT))
                prepend(MenuItem(icon = MenuIcon.SORT,
                    id = MenuId.SEARCH))
            }
        }
    }

    fun loadFavourite(){
        viewModelScope.launch {
            favouriteRepository.getFavourites().collect { favUsers ->
                Log.d("loadFavourite", "--> MenuItem: $favUsers")
                _favUserUiState.update { it.copy(favUsers = favUsers) }
            }
        }
    }

    fun reverseUsers(){
        // New LinkedList has to be created to update the list
        val userList = LinkedListImpl<User>()
        _homeUiState.value.users.let { users ->
            if (users is UiState.Success){
                users.data.let { userList ->
                    userList.reverse()
                    userList
                }
            }else{
                LinkedListImpl()
            }
        }.forEach { reversedList ->
            userList.append(reversedList)
        }

        _homeUiState.update {
            it.copy(users = UiState.Success(userList))
        }
    }

    fun setOnMenuClick(id: MenuId){
        when(id){
            MenuId.REVERSE -> {
                Log.d("MenuItem", "--> MenuItem: $id")
                reverseUsers()
            }
            MenuId.SORT -> {}
            MenuId.SWAP -> {}
            MenuId.SEARCH -> {

            }
            else -> {}
        }
    }
}