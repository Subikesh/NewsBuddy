package com.spacey.newsbuddy

import android.app.Application
import android.util.Log
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.spacey.newsbuddy.android.BuildConfig
import com.spacey.newsbuddy.common.Dependencies
import com.spacey.newsbuddy.common.PreferenceImpl
import com.spacey.newsbuddy.ui.isInternetConnected

class DependenciesImpl(private val context: Application) : Dependencies {
    override val preference = lazy { PreferenceImpl(context) }
    override val newsBuddyDatabase = lazy { com.spacey.newsbuddy.persistance.getNewsBuddyDatabase(context) }

    override fun isInternetConnected(): Boolean {
        return context.isInternetConnected()
    }

    override fun isAiFeaturesSupported(): Boolean {
        return FirebaseRemoteConfig.getInstance().run {
            if (getString(AI_FEATURES_ENABLED).isEmpty()) {
                Log.e("FirebaseRemoteConfig", "$AI_FEATURES_ENABLED key is not avl in firebase.")
                true // Default value if key not avl
            } else {
                getBoolean(AI_FEATURES_ENABLED)
            }
        }
    }

    override fun getNewsApiToken(): String = BuildConfig.NEWS_API_KEY

    companion object {
        const val AI_FEATURES_ENABLED = "ai_features_enabled"
    }
}