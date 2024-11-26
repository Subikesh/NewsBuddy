package com.spacey.newsbuddy.chat

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spacey.newsbuddy.genai.ChatWindow
import com.spacey.newsbuddy.serviceLocator
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {

    private val genAiRepository by lazy { serviceLocator.genAiRepository }

    private val _conversations = MutableStateFlow<ChatUiState>(ChatUiState.Loading)
    val conversation: StateFlow<ChatUiState> = _conversations

    private var date: String? = null

    fun startChat(date: String) {
        this.date = date
        _conversations.value = ChatUiState.Loading
        viewModelScope.launch {
            val result = genAiRepository.startAiChat(date)
            if (result.isSuccess) {
                _conversations.value = ChatUiState.Success(result.getOrThrow())
            } else {
                _conversations.value = ChatUiState.Error(result.exceptionOrNull().toString())
            }
        }
    }

    fun chat(prompt: String) {
        if (conversation.value !is ChatUiState.Success) {
            _conversations.value = ChatUiState.Loading
        }
        if (date == null) {
            _conversations.value = ChatUiState.Error("Chat for this date is not started yet")
            return
        }
        viewModelScope.launch {
            val chatWindow = conversation.value
            if (chatWindow !is ChatUiState.Success) {
                startChat(date!!)
                return@launch
            }
            genAiRepository.chatWithAi(chatWindow.chatWindow, prompt).onCompletion {
                val convo = conversation.value
                if (convo is ChatUiState.Success) {
                    _conversations.value = convo.copy(isAiChatLoading = false)
                }
            }.collect { result ->
                if (result.isSuccess) {
                    _conversations.value = ChatUiState.Success(result.getOrThrow(), true)
                } else {
                    Log.e("Error", "Convo chat response failed", result.exceptionOrNull())
                    _conversations.value = ChatUiState.Error(result.exceptionOrNull().toString())
                }
            }
        }
    }

    fun stopThinking() {
        viewModelScope.coroutineContext.cancelChildren()
    }
}

sealed class ChatUiState {
    data object Loading : ChatUiState()
    data class Error(val message: String) : ChatUiState()
    data class Success(val chatWindow: ChatWindow, val isAiChatLoading: Boolean = false) : ChatUiState()
}