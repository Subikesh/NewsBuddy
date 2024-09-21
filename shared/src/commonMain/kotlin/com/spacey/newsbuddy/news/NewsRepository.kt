package com.spacey.newsbuddy.news

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.JsonElement

class NewsRepository(private val newsApiService: NewsApiService) {

    suspend fun getTodaysNews(): Result<JsonElement> = withContext(Dispatchers.IO) {
        runCatching {
            newsApiService.getTodaysTopHeadlines()
        }
    }
}