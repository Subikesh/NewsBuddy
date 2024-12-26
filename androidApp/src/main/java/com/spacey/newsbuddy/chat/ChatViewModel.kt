package com.spacey.newsbuddy.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spacey.newsbuddy.android.BuildConfig
import com.spacey.newsbuddy.genai.ChatWindow
import com.spacey.newsbuddy.serviceLocator
import com.spacey.newsbuddy.ui.getErrorMsgOrNull
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {

    private val genAiRepository by lazy { serviceLocator.genAiRepository }

    private val _conversation = MutableStateFlow<ChatUiState>(ChatUiState.Loading)
    val conversation: StateFlow<ChatUiState> = _conversation

    var date: String? = null
        private set

    fun startChat(date: String) {
        this.date = date
        _conversation.value = ChatUiState.Loading
        viewModelScope.launch {
            val result = genAiRepository.startAiChat(date)
            if (result.isSuccess) {
                _conversation.value = ChatUiState.Success(result.getOrThrow())
            } else {
                _conversation.value = ChatUiState.Error(result.getErrorMsgOrNull(BuildConfig.DEBUG)!!.first)
            }
        }
    }

    fun chat(prompt: String) {
        if (conversation.value !is ChatUiState.Success) {
            _conversation.value = ChatUiState.Loading
        }
        if (date == null) {
            _conversation.value = ChatUiState.Error("Chat for this date is not started yet")
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
                    _conversation.value = convo.copy(isAiChatLoading = false)
                }
            }.collect { result ->
                if (result.isSuccess) {
                    _conversation.value = ChatUiState.Success(result.getOrThrow(), true)
                } else {
                    _conversation.value = ChatUiState.Error(result.getErrorMsgOrNull(BuildConfig.DEBUG)!!.first)
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