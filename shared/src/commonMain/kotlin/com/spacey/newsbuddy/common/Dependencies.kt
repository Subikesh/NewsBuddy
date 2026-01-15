package com.spacey.newsbuddy.common

import com.spacey.newsbuddy.news.NewsTokenFetcher
import com.spacey.newsbuddy.persistance.AppPreference
import com.spacey.newsbuddy.persistance.NewsBuddyDatabase

interface Dependencies {
    val preference: Lazy<AppPreference>
    val newsBuddyDatabase: Lazy<NewsBuddyDatabase>
    val connectivityManager: Lazy<ConnectivityManager>
    val newsTokenFetcher: Lazy<NewsTokenFetcher>
    val featureFlagManager: Lazy<FeatureFlagManager>
}