package com.spacey.newsbuddy.common

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.spacey.newsbuddy.DependenciesImpl.Companion.AI_FEATURES_ENABLED

class FeatureFlagManagerImpl : FeatureFlagManager {
    private val remoteConfig : FirebaseRemoteConfig
        get() = FirebaseRemoteConfig.getInstance()

    override fun isAiFeaturesSupported(): Boolean {
        return remoteConfig.getString(AI_FEATURES_ENABLED).isNotEmpty()
    }
}