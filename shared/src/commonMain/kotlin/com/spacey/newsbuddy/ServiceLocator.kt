package com.spacey.newsbuddy

import com.spacey.newsbuddy.genai.GenerativeAiService
import com.spacey.newsbuddy.news.NewsApiService

lateinit var serviceLocator: ServiceLocator

class ServiceLocator(private val dependencies: Dependencies) {
    private val newsApiService by lazy { NewsApiService(dependencies) }
    internal val preference by lazy { dependencies.getPreference() }

    val generativeAiService by lazy { GenerativeAiService(dependencies) }
    val newsRepository by lazy { NewsRepository(newsApiService, generativeAiService) }

    companion object {
        fun initiate(dependencies: Dependencies) {
            serviceLocator = ServiceLocator(dependencies)
        }
    }
}