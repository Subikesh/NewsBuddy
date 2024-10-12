package com.spacey.newsbuddy

import com.spacey.newsbuddy.genai.ConversationAiService
import com.spacey.newsbuddy.genai.GenerativeAiService
import com.spacey.newsbuddy.news.NewsApiService
import com.spacey.newsbuddy.news.NewsDao

lateinit var serviceLocator: ServiceLocator

class ServiceLocator(private val dependencies: Dependencies) {
    private val newsApiService by lazy { NewsApiService(dependencies) }
    private val generativeAiService by lazy { GenerativeAiService(dependencies) }
    private val conversationAiService by lazy { ConversationAiService(dependencies) }

    private val newsDao: NewsDao by lazy { dependencies.getNewsBuddyDatabase().getNewsDao() }

    internal val preference by lazy { dependencies.getPreference() }

    val newsRepository by lazy { NewsRepository(newsApiService, generativeAiService, conversationAiService, newsDao) }

    companion object {
        fun initiate(dependencies: Dependencies) {
            serviceLocator = ServiceLocator(dependencies)
        }
    }
}