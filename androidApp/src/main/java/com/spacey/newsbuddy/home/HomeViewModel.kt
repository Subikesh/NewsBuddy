package com.spacey.newsbuddy.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spacey.newsbuddy.ListedUiState
import com.spacey.newsbuddy.genai.GenAiRepository
import com.spacey.newsbuddy.serviceLocator
import com.spacey.newsbuddy.toListedUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState.LOADING)
    val uiState: StateFlow<HomeUiState> = _uiState

    private val genAiRepository: GenAiRepository = serviceLocator.genAiRepository

    fun loadHome() {
        _uiState.value = HomeUiState.LOADING
        viewModelScope.launch {
            val chats = kotlin.runCatching { genAiRepository.getRecentChats() }.toListedUiState("No chats found")
            val summaries = kotlin.runCatching { genAiRepository.getRecentSummaries() }.toListedUiState("No summaries found")
            _uiState.value = HomeUiState(chats, summaries)
        }
    }
}

data class HomeUiState(val chatHistory: ListedUiState<String>, val summaryHistory: ListedUiState<String>) {
    companion object {
        val LOADING = HomeUiState(ListedUiState.Loading(), ListedUiState.Loading())
    }
}