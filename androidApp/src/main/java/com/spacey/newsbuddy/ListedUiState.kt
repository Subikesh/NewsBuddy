package com.spacey.newsbuddy

sealed class ListedUiState<out T> {
    data class Loading(val message: String = "") : ListedUiState<Nothing>()
    data class Error(val message: String) : ListedUiState<Nothing>()
    data class Success<T>(val resultList: List<T>) : ListedUiState<T>()
}

fun <T> Result<List<T>>.toListedUiState(): ListedUiState<T> {
    return if (isSuccess) {
        ListedUiState.Success(getOrThrow())
    } else {
        ListedUiState.Error(exceptionOrNull()?.message ?: "Some error occurred")
    }
}
