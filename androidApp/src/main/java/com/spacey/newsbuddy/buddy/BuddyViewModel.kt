package com.spacey.newsbuddy.buddy

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spacey.newsbuddy.genai.Conversation
import com.spacey.newsbuddy.serviceLocator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class BuddyViewModel : ViewModel() {

    private val _uiState: MutableStateFlow<BuddyScreenState> = MutableStateFlow(BuddyScreenState.Loading())
    val uiState: StateFlow<BuddyScreenState> = _uiState

    private val newsRepository = serviceLocator.newsRepository

    fun promptTodaysNews() {
        _uiState.value = BuddyScreenState.Loading()
        viewModelScope.launch {
            val newsConvo = newsRepository.getNewsConversation()
            _uiState.value = newsConvo.fold(onSuccess = {
                BuddyScreenState.Success(it)
            }, onFailure = {
                BuddyScreenState.Error(it.toString())
            })
        }
    }
}

sealed class BuddyScreenState {
    data class Loading(val message: String = ""): BuddyScreenState()
    data class Error(val message: String): BuddyScreenState()
    data class Success(val conversations: List<Conversation>): BuddyScreenState()
}

