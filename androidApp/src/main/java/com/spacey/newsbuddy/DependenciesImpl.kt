package com.spacey.newsbuddy

import android.app.Application
import com.spacey.newsbuddy.common.ConnectivityManagerImpl
import com.spacey.newsbuddy.common.Dependencies
import com.spacey.newsbuddy.common.FeatureFlagManagerImpl
import com.spacey.newsbuddy.common.PreferenceImpl
import com.spacey.newsbuddy.network.NewsApiTokenFetcher
import com.spacey.newsbuddy.persistance.getNewsBuddyDatabase

class DependenciesImpl(context: Application) : Dependencies {
    override val preference = lazy { PreferenceImpl(context) }
    override val newsBuddyDatabase = lazy { getNewsBuddyDatabase(context) }
    override val connectivityManager = lazy { ConnectivityManagerImpl(context) }
    override val newsTokenFetcher = lazy { NewsApiTokenFetcher() }
    override val featureFlagManager = lazy { FeatureFlagManagerImpl() }

    companion object {
        const val AI_FEATURES_ENABLED = "ai_features_enabled"
    }
}