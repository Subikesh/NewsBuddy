package com.spacey.newsbuddy.settings

import com.spacey.newsbuddy.persistance.Preference

object SettingsAccessor {
    private var dataSyncEnabledPref: String by Preference("data_sync_enabled", PermissionState.DISABLED.name)
    var dataSyncEnabled: PermissionState
        get() = PermissionState.valueOf(dataSyncEnabledPref)
        set(value) { dataSyncEnabledPref = value.name }

    /** Data sync time string in format "HH:mm" */
    var dataSyncTime: String by Preference("data_sync_time", "5:00")

    var notificationCount: Int by Preference("notification_count")

    var summaryFeatureEnabled: Boolean by Preference("summary_feature_enabled")

    val dataSyncTimePair: Pair<Int, Int>
        get() = dataSyncTime.split(':').map { it.toInt() }.let { it[0] to it[1] }
}

enum class PermissionState {
    ENABLED, DISABLED, DENIED
}