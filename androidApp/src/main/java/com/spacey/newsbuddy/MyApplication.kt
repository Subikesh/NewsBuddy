package com.spacey.newsbuddy

import android.app.Application
import com.spacey.newsbuddy.android.BuildConfig
import com.spacey.newsbuddy.base.AppPreference

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        ServiceLocator.initiate(object : Dependencies {
            override fun getNewsApiToken(): String = BuildConfig.NEWS_API_KEY
            override fun getGeminiApiToken(): String = BuildConfig.GEMINI_API_KEY
            override fun getPreference(): AppPreference {
                TODO("Not yet implemented")
            }
        })
    }
}