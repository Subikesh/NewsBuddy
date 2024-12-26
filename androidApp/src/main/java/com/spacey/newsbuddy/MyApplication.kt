package com.spacey.newsbuddy

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.spacey.newsbuddy.common.initiateFireBaseSdk

lateinit var remoteConfig: FirebaseRemoteConfig

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        initiateFireBaseSdk(this)
        ServiceLocator.initiate(DependenciesImpl(this))
        remoteConfig = Firebase.remoteConfig
        remoteConfig.fetchAndActivate()

        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(
            NotificationChannel(
                SYNC_NOTIFICATION_CHANNEL,
                "News and Gen AI daily sync",
                NotificationManager.IMPORTANCE_DEFAULT
            )
        )
    }

    companion object {
        const val SYNC_NOTIFICATION_CHANNEL = "NewsSyncNotifications"
    }
}