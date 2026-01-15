package com.spacey.newsbuddy.news

import com.spacey.newsbuddy.common.BaseApiService
import kotlinx.serialization.json.JsonObject

abstract class NewsNetworkService : BaseApiService() {
    abstract suspend fun getTodaysTopHeadlines(date: String): Result<JsonObject>
}