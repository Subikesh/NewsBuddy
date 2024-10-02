package com.spacey.newsbuddy.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spacey.newsbuddy.ListedUiState
import com.spacey.newsbuddy.genai.Conversation
import com.spacey.newsbuddy.serviceLocator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class BuddyViewModel : ViewModel() {

    private val _uiState: MutableStateFlow<ListedUiState<Conversation>> = MutableStateFlow(ListedUiState.Loading())
    val uiState: StateFlow<ListedUiState<Conversation>> = _uiState

    private val newsRepository = serviceLocator.newsRepository
    private val cacheDate = LocalDate.now().minusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE)

    fun promptTodaysNews(forceRefresh: Boolean = false) {
        _uiState.value = ListedUiState.Loading()
        viewModelScope.launch {
            val newsConvo = newsRepository.getNewsConversation(cacheDate, forceRefresh)
            _uiState.value = newsConvo.fold(onSuccess = {
                ListedUiState.Success(it)
            }, onFailure = {
                Log.e("Error", "Error in getting news conversation")
                it.printStackTrace()
                ListedUiState.Error(it.toString())
            })
        }
    }
}

