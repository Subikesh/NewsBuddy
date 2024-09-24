package com.spacey.newsbuddy.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spacey.newsbuddy.genai.Conversation
import com.spacey.newsbuddy.serviceLocator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class BuddyViewModel : ViewModel() {

    private val _uiState: MutableStateFlow<BuddyScreenState> = MutableStateFlow(BuddyScreenState.Loading())
    val uiState: StateFlow<BuddyScreenState> = _uiState

    private val newsRepository = serviceLocator.newsRepository
    private val cacheDate = LocalDate.now().minusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE)

    fun promptTodaysNews(forceRefresh: Boolean = false) {
        _uiState.value = BuddyScreenState.Loading()
        viewModelScope.launch {
            val newsConvo = newsRepository.getNewsConversation(cacheDate, forceRefresh)
            _uiState.value = newsConvo.fold(onSuccess = {
                BuddyScreenState.Success(it)
            }, onFailure = {
                Log.e("Error", "Error in getting news conversation")
                it.printStackTrace()
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

