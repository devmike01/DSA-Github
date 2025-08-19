package dev.gbenga.dsagithub.features.home

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dev.gbenga.dsa.collections.Queue
import dev.gbenga.dsa.collections.QueueImpl
import dev.gbenga.dsa.collections.list.LinkedList
import dev.gbenga.dsa.collections.list.LinkedListImpl
import dev.gbenga.dsa.collections.list.linearFilter
import dev.gbenga.dsa.collections.list.linearFind
import dev.gbenga.dsa.startWithIgnoreCase
import dev.gbenga.dsagithub.base.AppViewModel
import dev.gbenga.dsagithub.base.MenuIcon
import dev.gbenga.dsagithub.base.MenuId
import dev.gbenga.dsagithub.base.MenuItem
import dev.gbenga.dsagithub.base.UiState
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

    private val _menus = MutableSharedFlow<LinkedList<MenuItem>>( )
    val menus : SharedFlow<LinkedList<MenuItem>> = _menus.asSharedFlow() //LinkedListImpl<MenuItem>()

    private val _selectedPage = MutableStateFlow<Int>(0)
    val selectedPage : StateFlow<Int> = _selectedPage.asStateFlow()

    private val _userList : LinkedList<User> = LinkedListImpl()
    private var _endQueue: Queue<Int> = QueueImpl<Int>(ENDLESS_SCROLL_SIZE)
    private var oldMenus : LinkedList<MenuItem>? =null
    private var previousSearch : MenuId? = null

    companion object{
        const val ENDLESS_SCROLL_SIZE = 2
        const val REFRESH_SCREEN = "HomeViewModel.REFRESH_SCREEN"
        const val EXPAND_SEARCH = "HomeViewModel.EXPAND_SEARCH"
        const val CACHED_MENU ="HomeViewModel.CACHED_MENU"
        const val QUERY = "HomeViewModel.QUERY"
        const val CURRENT_PAGE = "HomeViewModel.CURRENT_PAGE"
    }


    fun changePage(page: Int=0){
        _selectedPage.value =savedState[CURRENT_PAGE] ?: page
    }

    fun setExpandSearch(){
        savedState[EXPAND_SEARCH] = !(savedState.get<Boolean>(EXPAND_SEARCH) == true)
    }

    fun getExpandSearch(): Boolean{
        return savedState.get<Boolean?>(EXPAND_SEARCH) == true
    }

    fun loadMoreGithubUsers(){
        if (_endQueue.isEmpty())return
        viewModelScope.launch {
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

    fun getSearchQuery(): String = savedState[QUERY] ?: ""

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
                _userList.linearFilter { it.login.startWithIgnoreCase(savedState[QUERY] ?: "") }.forEach {
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
        viewModelScope.launch {
            val cachedMenuItem = savedState.get<LinkedList<MenuItem>?>(CACHED_MENU)
            if (cachedMenuItem == null){
                _menus.emit(LinkedListImpl<MenuItem>().apply {
                    prepend(MenuItem(icon = MenuIcon.REVERSE,
                        id = MenuId.REVERSE))
                    prepend(MenuItem(icon = MenuIcon.SORT,
                        id = MenuId.SORT))
                    prepend(MenuItem(icon = MenuIcon.SEARCH,
                        id = MenuId.SEARCH))
                }.also {
                    oldMenus = it
                })
            }else{
                _menus.emit(cachedMenuItem)
            }
        }
    }

    fun loadFavourite(){
        viewModelScope.launch {
            favouriteRepository.getFavourites().collect { favUsers ->
                _favUserUiState.update { it.copy(favUsers = favUsers) }
            }
        }
    }

    internal fun reverseUsers() {
        val reversedList: LinkedList<User> = LinkedListImpl()
        _userList.reverse()
        _userList.forEach {
            reversedList.append(it)
        }

        // Update state
        _homeUiState.update {
            it.copy(users = UiState.Success(reversedList))
        }
    }

    internal fun sortUsers(){
        val sortedList : LinkedList<User> = LinkedListImpl()
        if (_userList.size() > 100){
            // TODO: Use Merge sort
        }else{
            _userList.bubbleSort { it.data.login.lowercase() > (it.next?.data?.login?.lowercase() ?: "") }
            _userList.forEach {
                sortedList.append(it)
            }
        }
        _homeUiState.update { it.copy(users = UiState.Success(sortedList)) }
    }


    fun setOnMenuClick(id: MenuId){
        viewModelScope.launch {
            when(id){
                MenuId.REVERSE -> {
                    reverseUsers()
                }
                MenuId.SORT -> {
                    sortUsers()
                }
                MenuId.SEARCH -> {

                    Log.d("menuItems", "oldMenus: -> $oldMenus")
                    previousSearch?.let {
                        savedState[CACHED_MENU] = null
                        loadMenus()
                        previousSearch = null
                    } ?: _menus.let { menus ->
                        previousSearch = MenuId.SEARCH
                        val updatedMenu = oldMenus?.map { item -> item.copy(
                            hide = item.id != MenuId.SEARCH,
                            icon = if (item.id == MenuId.SEARCH){
                                MenuIcon.CANCEL
                            }else{
                                MenuIcon.SEARCH
                            }
                        ) }
                        if (updatedMenu != null){
                            savedState[CACHED_MENU] = updatedMenu
                            _menus.emit(updatedMenu)
                        }
                    } //CANCEL

                    _action.emit(
                        HomeUiEvent(menuAction = MenuId.SEARCH, expandSearch = true)
                    )
                }

                MenuId.RESET -> {
                    loadMenus()
                }
                else -> {}
            }
        }
    }


    fun searchUsers(query: String){
       // val userList = _userList.clone()
        savedState[QUERY] = query
        viewModelScope.launch {
            _homeUiState.update {
                it.copy(users = UiState.Success(_userList.clone().linearFilter {
                    it.login.startWithIgnoreCase(query) }))
            }
        }
    }


}