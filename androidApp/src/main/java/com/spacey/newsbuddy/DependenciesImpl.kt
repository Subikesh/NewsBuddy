package com.spacey.newsbuddy

import android.app.Application
import com.spacey.newsbuddy.android.BuildConfig
import com.spacey.newsbuddy.persistance.AppPreference
import com.spacey.newsbuddy.common.Dependencies
import com.spacey.newsbuddy.persistance.NewsBuddyDatabase
import com.spacey.newsbuddy.common.PreferenceImpl

class DependenciesImpl(private val context: Application) : Dependencies {
    override val preference = lazy { PreferenceImpl(context) }
    override val newsBuddyDatabase = lazy { com.spacey.newsbuddy.persistance.getNewsBuddyDatabase(context) }

    override fun getNewsApiToken(): String = BuildConfig.NEWS_API_KEY
    override fun getGeminiApiToken(): String = BuildConfig.GEMINI_API_KEY
}