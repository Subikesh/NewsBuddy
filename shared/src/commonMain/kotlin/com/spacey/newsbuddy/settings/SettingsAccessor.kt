package com.spacey.newsbuddy.settings

import com.spacey.newsbuddy.persistance.Preference

object SettingsAccessor {
    private var dataSyncEnabledPref: String by Preference("data_sync_enabled", PermissionState.DISABLED.name)
    var dataSyncEnabled: PermissionState
        get() = PermissionState.valueOf(dataSyncEnabledPref)
        set(value) { dataSyncEnabledPref = value.name }

    var notificationCount: Int by Preference("notification_count")

    var summaryFeatureEnabled: Boolean by Preference("summary_feature_enabled")
}

enum class PermissionState {
    ENABLED, DISABLED, DENIED
}