package com.spacey.newsbuddy

import com.spacey.newsbuddy.common.Dependencies
import com.spacey.newsbuddy.datasync.DataSyncRepository
import com.spacey.newsbuddy.datasync.SyncDao
import com.spacey.newsbuddy.genai.ConversationAiService
import com.spacey.newsbuddy.genai.BuddyChatDao
import com.spacey.newsbuddy.genai.GenAiRepository
import com.spacey.newsbuddy.genai.GenerativeAiService
import com.spacey.newsbuddy.genai.SummaryDao
import com.spacey.newsbuddy.news.NewsApiService
import com.spacey.newsbuddy.news.NewsDao
import com.spacey.newsbuddy.news.NewsRepository
import com.spacey.newsbuddy.persistance.AppPreference
import com.spacey.newsbuddy.persistance.NewsBuddyDatabase

lateinit var serviceLocator: ServiceLocator

class ServiceLocator(private val dependencies: Dependencies) {
    private val newsApiService by lazy { NewsApiService(dependencies) }
    private val generativeAiService by lazy { GenerativeAiService(dependencies) }

    private val newsBuddyDatabase: NewsBuddyDatabase by lazy { dependencies.getNewsBuddyDatabase() }
    private val newsDao: NewsDao by lazy { newsBuddyDatabase.getNewsDao() }
    private val buddyChatDao: BuddyChatDao by lazy { newsBuddyDatabase.getGenAiDao() }
    private val summaryDao: SummaryDao by lazy { newsBuddyDatabase.getSummaryDao() }
    private val dataSyncDao: SyncDao by lazy { newsBuddyDatabase.getSyncDao() }

    internal val preference: AppPreference by lazy { dependencies.getPreference() }

    val newsRepository by lazy { NewsRepository(newsApiService, newsDao) }
    val genAiRepository by lazy { GenAiRepository(newsRepository, generativeAiService, buddyChatDao, summaryDao, dependencies) }
    val syncRepository by lazy { DataSyncRepository(dataSyncDao) }

    companion object {
        fun initiate(dependencies: Dependencies) {
            serviceLocator = ServiceLocator(dependencies)
        }
    }
}