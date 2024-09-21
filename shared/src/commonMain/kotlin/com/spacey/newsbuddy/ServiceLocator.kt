package com.spacey.newsbuddy

import com.spacey.newsbuddy.news.NewsApiService
import com.spacey.newsbuddy.news.NewsRepository

internal lateinit var serviceLocator: ServiceLocator

class ServiceLocator {
    fun initiate() {
        serviceLocator = this
    }

    private val newsApiService = NewsApiService()

    val newsRepository = NewsRepository(newsApiService)

}