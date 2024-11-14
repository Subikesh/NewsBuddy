package com.spacey.newsbuddy.settings.sync

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spacey.newsbuddy.ListedUiState
import com.spacey.newsbuddy.datasync.SyncEntry
import com.spacey.newsbuddy.serviceLocator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DataSyncViewModel : ViewModel() {

    private val _uiState: MutableStateFlow<ListedUiState<SyncEntry>> = MutableStateFlow(ListedUiState.Loading())
    val uiState: StateFlow<ListedUiState<SyncEntry>> = _uiState
    private val syncRepository = serviceLocator.syncRepository

    fun fetchLatestDataSyncs() {
        _uiState.value = ListedUiState.Loading()
        viewModelScope.launch {
            val result = syncRepository.getSyncData()
            _uiState.value = ListedUiState.Success(result)
        }
    }
}