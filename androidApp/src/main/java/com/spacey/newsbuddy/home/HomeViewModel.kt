package com.spacey.newsbuddy.home

import androidx.lifecycle.ViewModel
import com.spacey.newsbuddy.ListedUiState
import com.spacey.newsbuddy.NewsRepository
import com.spacey.newsbuddy.genai.ChatWindow
import com.spacey.newsbuddy.genai.NewsSummary
import com.spacey.newsbuddy.serviceLocator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class HomeViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState.LOADING)
    val uiState: StateFlow<HomeUiState> = _uiState

    private val newsRepository: NewsRepository = serviceLocator.newsRepository

    fun loadHome() {
        TODO("To be implemented")
    }

}

data class HomeUiState(val chatHistory: ListedUiState<ChatWindow>, val summaryHistory: ListedUiState<NewsSummary>) {
    companion object {
        val LOADING = HomeUiState(ListedUiState.Loading(), ListedUiState.Loading())
    }
}