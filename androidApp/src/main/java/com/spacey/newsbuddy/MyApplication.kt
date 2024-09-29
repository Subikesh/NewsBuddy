package com.spacey.newsbuddy

import android.app.Application
import com.spacey.newsbuddy.android.BuildConfig
import com.spacey.newsbuddy.base.AppPreference
import com.spacey.newsbuddy.base.PreferenceImpl

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        ServiceLocator.initiate(DependenciesImpl(this))
    }
}