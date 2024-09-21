package com.spacey.newsbuddy.news

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class NewsRepository(private val newsApiService: NewsApiService) {
    suspend fun getTodaysNews(): Result<JsonElement> = withContext(Dispatchers.IO) {
        try {
            newsApiService.getTodaysTopHeadlines().also {
                if (it.getOrThrow().jsonObject.getValue("totalResults").jsonPrimitive.int == 0) {
                    throw Exception("Empty articles list was returned from news API")
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}