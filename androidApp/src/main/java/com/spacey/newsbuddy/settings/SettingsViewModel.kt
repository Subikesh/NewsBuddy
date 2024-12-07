package com.spacey.newsbuddy.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import com.spacey.newsbuddy.workers.cancelDataSync
import com.spacey.newsbuddy.workers.scheduleDataSync
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Calendar

class SettingsViewModel : ViewModel() {
    private val _syncState = MutableStateFlow(
        SettingsUiState(
            SettingsAccessor.summaryFeatureEnabled,
            SettingsAccessor.dataSyncEnabled,
            SettingsAccessor.dataSyncTimePair.first,
            SettingsAccessor.dataSyncTimePair.second
        )
    )
    val syncState: StateFlow<SettingsUiState> = _syncState

    fun enableSummary() {
        SettingsAccessor.summaryFeatureEnabled = true
        _syncState.value = syncState.value.copy(summaryFeatureEnabled = true)
    }

    fun disableSummary() {
        SettingsAccessor.summaryFeatureEnabled = false
        _syncState.value = syncState.value.copy(summaryFeatureEnabled = false)
    }

    fun enableDataSync(context: Context) {
        val previousTime = SettingsAccessor.dataSyncTimePair
        enableDataSync(context, previousTime.first, previousTime.second)
    }

    fun enableDataSync(context: Context, hour: Int, minute: Int) {
        SettingsAccessor.dataSyncEnabled = PermissionState.ENABLED
        SettingsAccessor.dataSyncTime = "$hour:$minute"
        scheduleDataSync(context, getNextTime(hour, minute))
        _syncState.value = syncState.value.copy(syncState = PermissionState.ENABLED, syncHour = hour, syncMinute = minute)
    }

    fun disableSync(context: Context) {
        SettingsAccessor.dataSyncEnabled = PermissionState.DISABLED
        cancelDataSync(context)
        _syncState.value = syncState.value.copy(syncState = PermissionState.DISABLED)
    }

    fun denyPermission() {
        _syncState.value = syncState.value.copy(syncState = PermissionState.DENIED)
        SettingsAccessor.dataSyncEnabled = PermissionState.DENIED
    }

    private fun getNextTime(hour: Int, minute: Int): Calendar {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            if (before(Calendar.getInstance())) {
                add(Calendar.DATE, 1)
            }
        }
        return calendar
    }
}

data class SettingsUiState(
    val summaryFeatureEnabled: Boolean,
    val syncState: PermissionState,
    val syncHour: Int,
    val syncMinute: Int
)