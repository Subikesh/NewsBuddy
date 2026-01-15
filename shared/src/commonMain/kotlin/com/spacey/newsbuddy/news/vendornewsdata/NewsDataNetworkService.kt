package com.spacey.newsbuddy.news.vendornewsdata

import com.spacey.newsbuddy.news.NewsNetworkService
import kotlinx.serialization.json.JsonObject

class NewsDataNetworkService : NewsNetworkService() {
    override suspend fun getTodaysTopHeadlines(date: String): Result<JsonObject> {
        TODO("Not yet implemented")
    }

}