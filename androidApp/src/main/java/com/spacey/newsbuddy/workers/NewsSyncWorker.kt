package com.spacey.newsbuddy.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.spacey.newsbuddy.serviceLocator
import com.spacey.newsbuddy.ui.getLatestDate

class NewsSyncWorker(context: Context, workerParams: WorkerParameters) : CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result {
        val date = getLatestDate()
        val newsResult = serviceLocator.newsRepository.getTodaysNews(date)
        val summaryResult = serviceLocator.genAiRepository.getNewsSummary(date)
        val chatResult = serviceLocator.genAiRepository.startAiChat(date)
        return if (newsResult.isSuccess && summaryResult.isSuccess && chatResult.isSuccess) {
            Result.success()
        } else {
            Result.retry()
        }
    }
}