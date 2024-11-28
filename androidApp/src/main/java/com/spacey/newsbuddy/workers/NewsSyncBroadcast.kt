package com.spacey.newsbuddy.workers

import android.app.AlarmManager
import android.app.Application.ALARM_SERVICE
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import java.time.LocalDateTime
import java.util.Calendar
import java.util.concurrent.TimeUnit
import kotlin.time.Duration.Companion.days

class NewsSyncBroadcast : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val workConstraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val newsSyncWorker = OneTimeWorkRequestBuilder<NewsSyncWorker>()
            .setConstraints(workConstraints)
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 10, TimeUnit.MINUTES)
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .build()
        val workManager = WorkManager.getInstance(context)
        workManager.enqueue(newsSyncWorker)
    }

    companion object {
        const val WORK_ID = "NewsSync"
    }
}

fun scheduleDataSync(context: Context, hour: Int = 4, minute: Int = 0) {
    val alarmManager = context.getSystemService(ALARM_SERVICE) as AlarmManager
    val pendingIntent = PendingIntent.getBroadcast(
        context,
        0,
        Intent(context, NewsSyncBroadcast::class.java),
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
    alarmManager.setRepeating(
        AlarmManager.RTC_WAKEUP,
        getTimeNextMorning(hour, minute).timeInMillis,
        1.days.inWholeMilliseconds,
        pendingIntent
    )
}

private fun getTimeNextMorning(hour: Int, minute: Int): Calendar {
    val calendar = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, minute)
        set(Calendar.SECOND, 0)
        if (before(Calendar.getInstance())) {
            add(Calendar.DATE, 1)
        }
    }
    return calendar
}
