package com.spacey.newsbuddy.news

import com.spacey.newsbuddy.base.BaseApiService
import io.ktor.client.call.body
import io.ktor.client.request.parameter
import kotlinx.serialization.json.JsonElement

class NewsApiService : BaseApiService() {
    suspend fun getTodaysTopHeadlines(): JsonElement =
        getApiCall(BASE_URL + "top-headlines") {
            parameter("sources", "google-news-api")
        }.body()
}

const val BASE_URL = "https://newsapi.org/v2/"