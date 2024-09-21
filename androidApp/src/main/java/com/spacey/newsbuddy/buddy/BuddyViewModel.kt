package com.spacey.newsbuddy.buddy

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spacey.newsbuddy.genai.Conversation
import com.spacey.newsbuddy.serviceLocator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class BuddyViewModel : ViewModel() {

    private val _conversationList: MutableStateFlow<List<Conversation>> = MutableStateFlow(emptyList())
    val conversationList: StateFlow<List<Conversation>> = _conversationList

    private val generativeAiService = serviceLocator.generativeAiService

    fun promptTodaysNews() {
        viewModelScope.launch {
            _conversationList.value = generativeAiService.promptTodaysNews()
        }
    }
}

