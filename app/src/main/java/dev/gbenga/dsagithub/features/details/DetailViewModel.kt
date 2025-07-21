package dev.gbenga.dsagithub.features.details

import androidx.lifecycle.viewModelScope
import dev.gbenga.dsagithub.base.AppViewModel
import dev.gbenga.dsagithub.base.UiState
import dev.gbenga.dsagithub.data.database.Favourite
import dev.gbenga.dsagithub.features.home.HomeRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DetailViewModel(private val repository: DetailRepository,
                      private val homeRepository: HomeRepository) : AppViewModel() {

    private val _details = MutableStateFlow<DetailsUiState>(DetailsUiState())
    val details : StateFlow<DetailsUiState> = _details

    private val _favouriteStatus = MutableSharedFlow<UiState<String>>()
    val favouriteStatus : SharedFlow<UiState<String>> = _favouriteStatus.asSharedFlow()


    fun populateDetailsTabContent(userId: String?){
        userId?.let {
            viewModelScope.launch {
                val userRepos = repository.getUserRepos(it.toString())
                userRepos.getOrNull()?.let{ userRepos ->
                    _details.update { it.copy(
                        userRepos = UiState.Success(userRepos)
                    ) }
                }

                userRepos.exceptionOrNull()?.message?.let { msg ->
                    _details.update { it.copy(
                        userRepos = UiState.Error(msg))
                    }
                }
            }
        }
    }

    fun favoriteUser(userName: String, avatarUrl: String){
        viewModelScope.launch {
            val favourite = Favourite(userName = userName, avatarUrl = avatarUrl)
            val result = homeRepository.addToFavourites(favourite)
            val uiState = result.fold(
                onSuccess = { UiState.Success(it)},
                onFailure = { UiState.Error(it.message ?: "An unknown error occurred")}
            )
            _favouriteStatus.tryEmit(uiState)
        }
    }
}