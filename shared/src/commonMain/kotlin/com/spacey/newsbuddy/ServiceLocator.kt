package com.spacey.newsbuddy

import com.spacey.newsbuddy.common.Dependencies
import com.spacey.newsbuddy.genai.ConversationAiService
import com.spacey.newsbuddy.genai.GenAiDao
import com.spacey.newsbuddy.genai.GenAiRepository
import com.spacey.newsbuddy.genai.GenerativeAiService
import com.spacey.newsbuddy.news.NewsApiService
import com.spacey.newsbuddy.news.NewsDao
import com.spacey.newsbuddy.news.NewsRepository
import com.spacey.newsbuddy.persistance.AppPreference
import com.spacey.newsbuddy.persistance.NewsBuddyDatabase

lateinit var serviceLocator: ServiceLocator

class ServiceLocator(private val dependencies: Dependencies) {
    private val newsApiService by lazy { NewsApiService(dependencies) }
    private val generativeAiService by lazy { GenerativeAiService(dependencies) }
    private val conversationAiService by lazy { ConversationAiService(dependencies) }

    private val newsBuddyDatabase: NewsBuddyDatabase by lazy { dependencies.getNewsBuddyDatabase() }
    private val newsDao: NewsDao by lazy { newsBuddyDatabase.getNewsDao() }
    private val genAiDao: GenAiDao by lazy { newsBuddyDatabase.getGenAiDao() }

    internal val preference: AppPreference by lazy { dependencies.getPreference() }

    val newsRepository by lazy { NewsRepository(newsApiService, newsDao) }
    val genAiRepository by lazy { GenAiRepository(newsRepository, generativeAiService, conversationAiService, genAiDao) }

    companion object {
        fun initiate(dependencies: Dependencies) {
            serviceLocator = ServiceLocator(dependencies)
        }
    }
}