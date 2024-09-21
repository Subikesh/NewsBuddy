package com.spacey.newsbuddy.news

import com.spacey.newsbuddy.Dependencies
import com.spacey.newsbuddy.base.BaseApiService
import io.ktor.client.call.body
import io.ktor.client.request.parameter
import io.ktor.http.isSuccess
import kotlinx.serialization.json.JsonObject

class NewsApiService(dependencies: Dependencies) : BaseApiService(dependencies) {
    suspend fun getTodaysTopHeadlines(): Result<JsonObject> {
        val response = getApiCall(BASE_URL + "top-headlines") {
            parameter("sources", "google-news-in")
        }
        return if (response.status.isSuccess()) {
            Result.success(response.body())
        } else {
            Result.failure(Exception(response.toString()))
        }
    }
}

const val BASE_URL = "https://newsapi.org/v2/"