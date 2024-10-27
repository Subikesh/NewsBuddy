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

    fun loadHome(navToChat: (String) -> Unit, navToSummary: (String) -> Unit) {
        _uiState.value = HomeUiState.LOADING
        viewModelScope.launch {
            val chats = kotlin.runCatching {
                genAiRepository.getRecentChats().map {
                    HomeBubble(it) {
                        navToChat(it)
                    }
                }
            }.toListedUiState("No chats found")
            val summaries = kotlin.runCatching {
                genAiRepository.getRecentSummaries().map {
                    HomeBubble(it) {
                        navToSummary(it)
                    }
                }
            }.toListedUiState("No summaries found")
            _uiState.value = HomeUiState(chats, summaries)
        }
    }
}

data class HomeUiState(
    val chatHistory: ListedUiState<HomeBubble>,
    val summaryHistory: ListedUiState<HomeBubble>
) {
    companion object {
        val LOADING = HomeUiState(ListedUiState.Loading(), ListedUiState.Loading())
    }
}

class HomeBubble(val title: String, val onClick: () -> Unit)