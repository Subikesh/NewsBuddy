package com.spacey.newsbuddy

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.spacey.newsbuddy.workers.NewsSyncWorker
import java.util.Calendar
import java.util.concurrent.TimeUnit

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        ServiceLocator.initiate(DependenciesImpl(this))

        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(NotificationChannel(
            SYNC_NOTIFICATION_CHANNEL,
            "News and Gen AI daily sync",
            NotificationManager.IMPORTANCE_DEFAULT
        ))

        val workConstraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val newsSyncWorker = PeriodicWorkRequestBuilder<NewsSyncWorker>(1, TimeUnit.DAYS)
            .setConstraints(workConstraints)
//            .setInitialDelay(calculateTimeTillNextMorning(4, 0), TimeUnit.MILLISECONDS)
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 10, TimeUnit.MINUTES)
            .build()
        WorkManager.getInstance(this)
            .enqueueUniquePeriodicWork("NewsSync", ExistingPeriodicWorkPolicy.KEEP, newsSyncWorker)
    }

    private fun calculateTimeTillNextMorning(hour: Int, minute: Int): Long {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DATE, 1)
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        calendar.set(Calendar.SECOND, 0)
        val now = Calendar.getInstance()
        return calendar.timeInMillis - now.timeInMillis
    }

    companion object {
        const val SYNC_NOTIFICATION_CHANNEL = "NewsSyncNotifications"
    }
}