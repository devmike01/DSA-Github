package dev.gbenga.dsagithub.base

sealed interface UiState<T> {
    data class Success<T>(val data: T): UiState<T>
    data class Error<T>(val errorMsg: String): UiState<T>
    data class Loading<T>(val title: String="") : UiState<T>
}