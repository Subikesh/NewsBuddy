package com.spacey.newsbuddy

sealed class ListedUiState<out T> {
    data class Loading(val message: String = "") : ListedUiState<Nothing>()
    data class Error(val message: String) : ListedUiState<Nothing>()
    data class Success<T>(val resultList: List<T>) : ListedUiState<T>()
}

fun <T> Result<List<T>>.toListedUiState(emptyMsg: String? = null): ListedUiState<T> {
    return if (isSuccess) {
        val res = getOrThrow()
        if (res.isEmpty() && emptyMsg != null) {
            return ListedUiState.Error(emptyMsg)
        }
        ListedUiState.Success(res)
    } else {
        ListedUiState.Error(exceptionOrNull()?.message ?: "Some error occurred")
    }
}
