package com.spacey.newsbuddy

import com.spacey.newsbuddy.news.NewsApiService
import com.spacey.newsbuddy.news.NewsRepository

lateinit var serviceLocator: ServiceLocator

class ServiceLocator {
    private val newsApiService = NewsApiService()

    val newsRepository = NewsRepository(newsApiService)

    companion object {
        fun initiate() {
            serviceLocator = ServiceLocator()
        }
    }
}