package com.spacey.newsbuddy.news

import com.spacey.newsbuddy.common.ConnectivityManager
import com.spacey.newsbuddy.common.Dependencies
import com.spacey.newsbuddy.common.NoInternetException
import com.spacey.newsbuddy.genai.NewsResponse
import com.spacey.newsbuddy.news.vendornewsapi.NewsApiNetworkService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class NewsRepository(
    private val newsApiService: NewsNetworkService,
    private val newsDao: NewsDao,
    private val connectivityManager: ConnectivityManager
) {

    suspend fun getTodaysNews(date: String, forceRefresh: Boolean = false): Result<NewsResponse> = withContext(Dispatchers.IO) {
        try {
            if (!connectivityManager.isInternetConnected()) {
                throw NoInternetException
            }
            val summary = runCatching { newsDao.getNewsResponse(date) }
            if (!forceRefresh && summary.isSuccess) {
                return@withContext summary
            }
            val news = newsApiService.getTodaysTopHeadlines(date)
            if (news.getOrThrow().jsonObject.getValue("totalResults").jsonPrimitive.int == 0) {
                throw Exception("Empty articles list was returned from news API")
            }
            newsDao.upsert(listOf(NewsResponse(date, news.toString())))
            kotlin.runCatching { newsDao.getNewsResponse(date) }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}