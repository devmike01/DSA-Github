package dev.gbenga.dsagithub.features.details

import dev.gbenga.dsagithub.base.AppViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class DetailViewModel : AppViewModel() {

    val _userContent = MutableStateFlow<DetailsUiState>(DetailsUiState())
    val userContent : StateFlow<DetailsUiState> = _userContent


}