package com.spacey.newsbuddy.chat

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spacey.newsbuddy.genai.ChatWindow
import com.spacey.newsbuddy.serviceLocator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ChatViewModel : ViewModel() {

    private val genAiRepository by lazy { serviceLocator.genAiRepository }

    private val _conversations = MutableStateFlow<ChatUiState>(ChatUiState.Loading)
    val conversation: StateFlow<ChatUiState> = _conversations

    private val yesterday = LocalDate.now().minusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE)

    fun startChat() {
        _conversations.value = ChatUiState.Loading
        viewModelScope.launch {
            val result = genAiRepository.startAiChat(yesterday)
            if (result.isSuccess) {
                _conversations.value = ChatUiState.Success(result.getOrThrow())
            } else {
                _conversations.value = ChatUiState.Error(result.exceptionOrNull().toString())
            }
        }
    }

    fun chat(prompt: String) {
        var current = conversation.value
//        val prompt = prompt.ifBlank { "Continue" }
        if (current is ChatUiState.Success) {
        // TODO: Add loading bubble
//            current = current.copy(current.conversations + listOf(ChatBubble(prompt, true), ChatBubble("", isUser = false, true)))
//            _conversations.value = current
        } else {
            _conversations.value = ChatUiState.Loading
        }
        viewModelScope.launch {
            val chatWindow = conversation.value
            if (chatWindow !is ChatUiState.Success) {
                startChat()
                return@launch
            }
            val result = genAiRepository.chatWithAi(chatWindow.chatWindow, prompt)
            if (result.isSuccess) {
                _conversations.value = ChatUiState.Success(result.getOrThrow())
            } else {
                Log.e("Error", "Convo chat response failed", result.exceptionOrNull())
                _conversations.value = ChatUiState.Error(result.exceptionOrNull().toString())
            }
        }
    }
}

sealed class ChatUiState {
    data object Loading : ChatUiState()
    data class Error(val message: String) : ChatUiState()
    data class Success(val chatWindow: ChatWindow) : ChatUiState()
}