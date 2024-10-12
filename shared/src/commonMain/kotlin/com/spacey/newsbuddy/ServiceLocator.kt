package com.spacey.newsbuddy

import com.spacey.newsbuddy.genai.ConversationAiService
import com.spacey.newsbuddy.genai.GenAiDao
import com.spacey.newsbuddy.genai.GenerativeAiService
import com.spacey.newsbuddy.news.NewsApiService
import com.spacey.newsbuddy.news.NewsDao

lateinit var serviceLocator: ServiceLocator

class ServiceLocator(private val dependencies: Dependencies) {
    private val newsApiService by lazy { NewsApiService(dependencies) }
    private val generativeAiService by lazy { GenerativeAiService(dependencies) }
    private val conversationAiService by lazy { ConversationAiService(dependencies) }

    private val newsBuddyDatabase by lazy { dependencies.getNewsBuddyDatabase() }
    private val newsDao: NewsDao by lazy { newsBuddyDatabase.getNewsDao() }
    private val genAiDao: GenAiDao by lazy { newsBuddyDatabase.getGenAiDao() }

    internal val preference by lazy { dependencies.getPreference() }

    val newsRepository by lazy { NewsRepository(newsApiService, generativeAiService, conversationAiService, newsDao, genAiDao) }

    companion object {
        fun initiate(dependencies: Dependencies) {
            serviceLocator = ServiceLocator(dependencies)
        }
    }
}