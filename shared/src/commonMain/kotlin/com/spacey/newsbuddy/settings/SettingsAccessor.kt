package com.spacey.newsbuddy.settings

import com.spacey.newsbuddy.persistance.Preference

object SettingsAccessor {
    var dataSyncEnabled: Boolean by Preference("data_sync_enabled")
}