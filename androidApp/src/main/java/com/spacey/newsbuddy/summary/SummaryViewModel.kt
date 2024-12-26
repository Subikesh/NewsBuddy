package com.spacey.newsbuddy.summary

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spacey.newsbuddy.ListedUiState
import com.spacey.newsbuddy.android.BuildConfig
import com.spacey.newsbuddy.genai.SummaryParagraph
import com.spacey.newsbuddy.serviceLocator
import com.spacey.newsbuddy.ui.getErrorMsgOrNull
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SummaryViewModel : ViewModel() {

    private val _uiState: MutableStateFlow<ListedUiState<SummaryParagraph>> = MutableStateFlow(ListedUiState.Loading())
    val uiState: StateFlow<ListedUiState<SummaryParagraph>> = _uiState

    private val genAiRepository = serviceLocator.genAiRepository

    fun promptNews(date: String, forceRefresh: Boolean = false) {
        _uiState.value = ListedUiState.Loading()
        viewModelScope.launch {
            val newsConvo = genAiRepository.getNewsSummary(date, forceRefresh)
            _uiState.value = newsConvo.fold(onSuccess = {
                ListedUiState.Success(it)
            }, onFailure = {
                Log.e("Error", "Error in getting news conversation")
                it.printStackTrace()
                ListedUiState.Error(newsConvo.getErrorMsgOrNull(BuildConfig.DEBUG)!!.first)
            })
        }
    }
}

