package com.spacey.newsbuddy

import android.content.Context
import com.spacey.newsbuddy.android.BuildConfig
import com.spacey.newsbuddy.base.AppPreference
import com.spacey.newsbuddy.base.PreferenceImpl

class DependenciesImpl(private val context: Context) : Dependencies {
    override fun getNewsApiToken(): String = BuildConfig.NEWS_API_KEY
    override fun getGeminiApiToken(): String = BuildConfig.GEMINI_API_KEY
    override fun getPreference(): AppPreference = PreferenceImpl(context)
}