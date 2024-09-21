package com.spacey.newsbuddy

import android.app.Application
import com.spacey.newsbuddy.android.BuildConfig

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        ServiceLocator.initiate(object : Dependencies {
            override fun getNewsApiToken(): String = BuildConfig.NEWS_API_KEY
        })
    }
}