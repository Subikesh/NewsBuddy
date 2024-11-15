package com.spacey.newsbuddy.workers

import android.annotation.SuppressLint
import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.spacey.newsbuddy.MyApplication
import com.spacey.newsbuddy.android.BuildConfig
import com.spacey.newsbuddy.android.R
import com.spacey.newsbuddy.datasync.SyncEntry
import com.spacey.newsbuddy.genai.ChatWindow
import com.spacey.newsbuddy.genai.SummaryParagraph
import com.spacey.newsbuddy.news.NewsResponse
import com.spacey.newsbuddy.serviceLocator
import com.spacey.newsbuddy.ui.getLatestDate
import com.spacey.newsbuddy.ui.isNotificationAllowed

class NewsSyncWorker(context: Context, workerParams: WorkerParameters) :
    CoroutineWorker(context, workerParams) {

    private val syncRepository = serviceLocator.syncRepository

    @SuppressLint("MissingPermission")
    override suspend fun doWork(): Result {
        setForeground(ForegroundInfo(
            0,
            createNotification(
                "Data Sync in Progress",
                "Latest news syncing. Infinite wisdom awaits..."
            )
        ))
        val date = getLatestDate()
        val newsResult = serviceLocator.newsRepository.getTodaysNews(date)
        val chatResult = serviceLocator.genAiRepository.startAiChat(date)
        val summaryResult = serviceLocator.genAiRepository.getNewsSummary(date)

        makeSyncEntry(newsResult, summaryResult, chatResult)
        return if (newsResult.isSuccess && summaryResult.isSuccess && chatResult.isSuccess) {
            if (applicationContext.isNotificationAllowed()) {
                NotificationManagerCompat.from(applicationContext).notify(
                    0, createNotification(
                        "News Sync Completed!",
                        "Latest news synced and I cant wait to talk about it!"
                    )
                )
            }
            Result.success()
        } else {
            if (applicationContext.isNotificationAllowed() && BuildConfig.DEBUG) {
                NotificationManagerCompat.from(applicationContext).notify(
                    0, createNotification(
                        "News Sync Failed!",
                        "News result: ${newsResult.isSuccess}; Summary result: ${summaryResult.isSuccess}; Chat Result: ${chatResult.isSuccess}"
                    )
                )
            }
            Result.retry()
        }
    }

    private suspend fun makeSyncEntry(
        newsResult: kotlin.Result<NewsResponse>,
        summaryResult: kotlin.Result<List<SummaryParagraph>>,
        chatResult: kotlin.Result<ChatWindow>
    ) {
        val syncEntry = SyncEntry(
            System.currentTimeMillis(),
            newsResult.isSuccess.toString(),
            summaryResult.exceptionOrNull()?.message ?: true.toString(),
            chatResult.exceptionOrNull()?.message ?: true.toString(),
        )
        syncRepository.insert(syncEntry)
    }

    private fun createNotification(title: String, content: String): Notification {
        // TODO: Open date's content rather than activity
        val contentIndent = PendingIntent.getActivity(
            applicationContext,
            0,
            Intent(applicationContext, MyApplication::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )

        return Notification.Builder(applicationContext, MyApplication.SYNC_NOTIFICATION_CHANNEL)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(content)
            .setContentIntent(contentIndent)
            .setAutoCancel(true)
            .setShowWhen(true)
            .build()
    }
}