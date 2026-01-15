package com.spacey.newsbuddy.news.vendornewsapi

import com.spacey.newsbuddy.common.BASE_URL
import com.spacey.newsbuddy.common.NEWS_LANGUAGE
import com.spacey.newsbuddy.common.NEWS_PAGE_SIZE
import com.spacey.newsbuddy.common.BaseApiService
import com.spacey.newsbuddy.news.NewsNetworkService
import com.spacey.newsbuddy.news.NewsTokenFetcher
import io.ktor.client.call.body
import io.ktor.client.request.parameter
import io.ktor.http.isSuccess
import kotlinx.serialization.json.JsonObject

class NewsApiNetworkService(private val tokenFetcher: NewsTokenFetcher) : NewsNetworkService() {

    override suspend fun getTodaysTopHeadlines(date: String): Result<JsonObject> {
        val response = getApiCall(BASE_URL + "everything", tokenFetcher.get()) {
            parameter("sources", "the-times-of-india,google-news-in")
//            parameter("sortBy", "popularity")
            parameter("from", date)
            parameter("pageSize", NEWS_PAGE_SIZE)
            parameter("language", NEWS_LANGUAGE)
        }
        return if (response.status.isSuccess()) {
            Result.success(response.body())
        } else {
            Result.failure(Exception(response.toString()))
        }
    }
}