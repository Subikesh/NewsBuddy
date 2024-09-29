package com.spacey.newsbuddy

import com.spacey.newsbuddy.genai.ConversationAiService
import com.spacey.newsbuddy.genai.GenerativeAiService
import com.spacey.newsbuddy.news.NewsApiService

lateinit var serviceLocator: ServiceLocator

class ServiceLocator(private val dependencies: Dependencies) {
    private val newsApiService by lazy { NewsApiService(dependencies) }
    private val generativeAiService by lazy { GenerativeAiService(dependencies) }
    private val conversationAiService by lazy { ConversationAiService(dependencies) }
    internal val preference by lazy { dependencies.getPreference() }

    val newsRepository by lazy { NewsRepository(newsApiService, generativeAiService) }

    companion object {
        fun initiate(dependencies: Dependencies) {
            serviceLocator = ServiceLocator(dependencies)
        }
    }
}