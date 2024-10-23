package com.spacey.newsbuddy.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import kotlin.time.Duration.Companion.seconds

class HomeViewModel : ViewModel() {

    private val _uiState: MutableStateFlow<HomeUiState> = MutableStateFlow(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState

    fun fetchHomeData() {
        _uiState.value = HomeUiState.Loading
        viewModelScope.launch {
            delay(1.seconds)
            _uiState.value = HomeUiState.Success(listOf(SummaryData("Yesterday's news", LocalDate.now())))
        }
    }
}

sealed class HomeUiState {
    data object Loading : HomeUiState()

    data class Success(val summaryList: List<SummaryData>) : HomeUiState()
    data class Failure(val error: String) : HomeUiState()
}

data class SummaryData(val heading: String, val date: LocalDate)