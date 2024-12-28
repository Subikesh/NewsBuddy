package com.spacey.newsbuddy.settings.sync

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import androidx.work.await
import com.spacey.newsbuddy.ListedUiState
import com.spacey.newsbuddy.datasync.SyncEntry
import com.spacey.newsbuddy.serviceLocator
import com.spacey.newsbuddy.workers.NewsSyncWorker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.time.Duration.Companion.seconds

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

    fun syncNow(context: Context) {
        _uiState.value = ListedUiState.Loading()
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                WorkManager.getInstance(context).enqueue(OneTimeWorkRequestBuilder<NewsSyncWorker>().run {
                    setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                    build()
                }).await()
            }
            fetchLatestDataSyncs()
        }
    }
}