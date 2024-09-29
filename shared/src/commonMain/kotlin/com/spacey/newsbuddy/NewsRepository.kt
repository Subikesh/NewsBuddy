package com.spacey.newsbuddy

import com.spacey.newsbuddy.base.Preference
import com.spacey.newsbuddy.genai.Conversation
import com.spacey.newsbuddy.genai.GenerativeAiService
import com.spacey.newsbuddy.news.NewsApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class NewsRepository(private val newsApiService: NewsApiService, private val generativeAiService: GenerativeAiService) {

    private var cacheDate: String by Preference(Preference.CACHE_DATE)
    private var newsPreference: String by Preference(Preference.NEWS_RESPONSE)
    private var aiResponse: String by Preference(Preference.AI_RESPONSE)

    suspend fun getNewsConversation(today: String, forceRefresh: Boolean = false): Result<List<Conversation>> {
        return if (forceRefresh || aiResponse.isEmpty() || cacheDate != today) {
            log("Date", "Response for cacheDate: $today")
            val news = getTodaysNews(today)
            news.convertCatching {
                log("News", "News response: $it")
                generativeAiService.runPrompt(it).map { aiMsg ->
                    aiResponse = aiMsg
                    cacheDate = today
                    parseJson(aiMsg)
                }
            }
        } else {
            Result.success(parseJson(aiResponse))
        }
    }

    suspend fun getTodaysNews(today: String, forceRefresh: Boolean = false): Result<String> = withContext(Dispatchers.IO) {
        try {
            if (forceRefresh || newsPreference.isEmpty() || cacheDate != today) {
                newsApiService.getTodaysTopHeadlines(today).let {
                    if (it.getOrThrow().jsonObject.getValue("totalResults").jsonPrimitive.int == 0) {
                        throw Exception("Empty articles list was returned from news API")
                    }
                    it.map { result ->
                        newsPreference = result.toString()
                    }
                }
            }
            Result.success(newsPreference)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun parseJson(json: String): List<Conversation> {
        val jsonObject: JsonObject = Json.decodeFromString(json.escapeAiContent())
        return Json.decodeFromJsonElement(jsonObject[GenerativeAiService.NEWS_CURATION]!!)
    }

    private fun String.escapeAiContent(): String {
        return replace("\\$", "$")
    }
}