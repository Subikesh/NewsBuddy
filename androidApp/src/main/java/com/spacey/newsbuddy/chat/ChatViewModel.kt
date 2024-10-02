package com.spacey.newsbuddy.chat

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spacey.newsbuddy.ListedUiState
import com.spacey.newsbuddy.foldAsString
import com.spacey.newsbuddy.serviceLocator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ChatViewModel : ViewModel() {

    private val newsRepository by lazy { serviceLocator.newsRepository }

    private val _conversations = MutableStateFlow<ListedUiState<ChatBox>>(ListedUiState.Loading())
    val conversation: StateFlow<ListedUiState<ChatBox>> = _conversations

    private val yesterday = LocalDate.now().minusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE)

    fun startChat() {
        _conversations.value = ListedUiState.Loading()
        viewModelScope.launch {
            val result = newsRepository.startAiChat(yesterday)
            if (result.isSuccess) {
                _conversations.value = ListedUiState.Success(listOf(ChatBox(result.getOrThrow().foldAsString(), false)))
            } else {
                _conversations.value = ListedUiState.Error(result.exceptionOrNull().toString())
            }
        }
    }

    fun chat(prompt: String) {
        var current = conversation.value
        if (current is ListedUiState.Success) {
            current = current.copy(current.conversations + listOf(ChatBox(prompt, true), ChatBox("", isUser = false, true)))
            _conversations.value = current
        } else {
            _conversations.value = ListedUiState.Loading()
        }
        viewModelScope.launch {
            val result = newsRepository.chatWithAi(prompt)
            if (result.isSuccess) {
                if (current is ListedUiState.Success) {
                    _conversations.value = current.copy(current.conversations.dropLast(1) + ChatBox(result.getOrThrow().foldAsString(), false))
                } else {
                    _conversations.value = ListedUiState.Error(result.getOrThrow().foldAsString())
                }
            } else {
                Log.e("Error", "Convo chat response failed", result.exceptionOrNull())
                _conversations.value = ListedUiState.Error(result.exceptionOrNull().toString())
            }
        }
    }
}

data class ChatBox(val text: String, val isUser: Boolean, val isLoading: Boolean = false)