package com.spacey.newsbuddy.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import com.spacey.newsbuddy.workers.cancelDataSync
import com.spacey.newsbuddy.workers.scheduleDataSync
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SettingsViewModel : ViewModel() {
    private val _syncState = MutableStateFlow(SettingsUiState(SettingsAccessor.dataSyncEnabled, SettingsAccessor.summaryFeatureEnabled))
    val syncState: StateFlow<SettingsUiState> = _syncState

    fun enableSummary() {
        _syncState.value = syncState.value.copy(summaryFeatureEnabled = true)
    }

    fun disableSummary() {
        _syncState.value = syncState.value.copy(summaryFeatureEnabled = false)
    }

    fun enableDataSync(context: Context) {
        SettingsAccessor.dataSyncEnabled = PermissionState.ENABLED
        _syncState.value = syncState.value.copy(syncState = PermissionState.ENABLED)
        scheduleDataSync(context)
    }

    fun disableSync(context: Context) {
        SettingsAccessor.dataSyncEnabled = PermissionState.DISABLED
        _syncState.value = syncState.value.copy(syncState = PermissionState.DISABLED)
        cancelDataSync(context)
    }

    fun denyPermission() {
        _syncState.value = syncState.value.copy(syncState = PermissionState.DENIED)
        SettingsAccessor.dataSyncEnabled = PermissionState.DENIED
    }
}

data class SettingsUiState(
    val syncState: PermissionState,
    val summaryFeatureEnabled: Boolean
)