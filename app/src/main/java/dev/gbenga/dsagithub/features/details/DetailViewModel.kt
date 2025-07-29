package dev.gbenga.dsagithub.features.details

import androidx.lifecycle.viewModelScope
import dev.gbenga.dsagithub.base.AppViewModel
import dev.gbenga.dsagithub.base.UiState
import dev.gbenga.dsagithub.data.database.Favourite
import dev.gbenga.dsagithub.features.home.FavouriteRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DetailViewModel(private val repository: DetailRepository,
                      private val favouriteRepository: FavouriteRepository) : AppViewModel() {

    private val _details = MutableStateFlow<DetailsUiState>(DetailsUiState())
    val details : StateFlow<DetailsUiState> = _details

    private val _message = MutableSharedFlow<DetailMessenger>()
    val message : SharedFlow<DetailMessenger> = _message.asSharedFlow()

    companion object{
        const val UNKNOWN_ERROR = "An unknown error occurred"
    }


    fun populateDetailsTabContent(userId: String?){
        userId?.let {
            viewModelScope.launch {
                val userRepos = repository.getUserRepos(it.toString())
                val uiState = userRepos.fold(
                    onSuccess = { UiState.Success(it)},
                    onFailure = { UiState.Error(it.message ?: "")}
                )
                _details.update { it.copy(userRepos = uiState)
                }
            }
        }
    }


    fun unFavoriteUser(userName: String?){
        if (userName ==null)return
        viewModelScope.launch {
            val result = favouriteRepository.removeFavourite(userName)
            val message = result.fold(
                onSuccess = {
                    // persist message for previous screen
                    DetailMessenger(action = MessengerAction.CLOSE_SCREEN)},
                onFailure = { DetailMessenger(it.message ?: UNKNOWN_ERROR)}
            )

            _message.emit(message)
        }
    }

    fun favoriteUser(userName: String?, avatarUrl: String){
        if (userName ==null)return
        viewModelScope.launch {
            val favourite = Favourite(userName = userName, avatarUrl = avatarUrl)
            val result = favouriteRepository.addToFavourites(favourite)
            val uiState = result.fold(
                onSuccess = { DetailMessenger(it)},
                onFailure = { DetailMessenger(it.message ?: UNKNOWN_ERROR)}
            )
            _message.tryEmit(uiState)
        }
    }
}