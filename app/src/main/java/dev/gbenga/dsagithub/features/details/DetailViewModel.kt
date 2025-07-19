package dev.gbenga.dsagithub.features.details

import androidx.lifecycle.viewModelScope
import dev.gbenga.dsagithub.base.AppViewModel
import dev.gbenga.dsagithub.base.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DetailViewModel(private val repository: DetailRepository) : AppViewModel() {

    private val _details = MutableStateFlow<DetailsUiState>(DetailsUiState())
    val details : StateFlow<DetailsUiState> = _details


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

    fun favoriteUser(){

    }
}