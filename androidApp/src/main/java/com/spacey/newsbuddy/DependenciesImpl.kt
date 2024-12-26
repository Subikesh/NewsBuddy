package com.spacey.newsbuddy

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.spacey.newsbuddy.android.BuildConfig
import com.spacey.newsbuddy.common.Dependencies
import com.spacey.newsbuddy.common.PreferenceImpl

class DependenciesImpl(private val context: Application) : Dependencies {
    override val preference = lazy { PreferenceImpl(context) }
    override val newsBuddyDatabase = lazy { com.spacey.newsbuddy.persistance.getNewsBuddyDatabase(context) }

    override fun isInternetConnected(): Boolean {
        val connectivity = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return connectivity.activeNetworkInfo?.isConnected == true
    }

    override fun isAiFeaturesSupported(): Boolean {
        return FirebaseRemoteConfig.getInstance().getBoolean(AI_FEATURES_ENABLED)
    }

    override fun getNewsApiToken(): String = BuildConfig.NEWS_API_KEY

    companion object {
        const val AI_FEATURES_ENABLED = "ai_features_enabled"
    }
}