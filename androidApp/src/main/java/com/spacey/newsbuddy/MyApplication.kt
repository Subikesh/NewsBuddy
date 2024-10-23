package com.spacey.newsbuddy

import android.app.Application

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        ServiceLocator.initiate(DependenciesImpl(this))
    }
}