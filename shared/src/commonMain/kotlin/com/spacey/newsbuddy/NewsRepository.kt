package com.spacey.newsbuddy

import com.spacey.newsbuddy.base.Preference
import com.spacey.newsbuddy.genai.Conversation
import com.spacey.newsbuddy.genai.ConversationAiService
import com.spacey.newsbuddy.genai.GenAiDao
import com.spacey.newsbuddy.genai.GenerativeAiService
import com.spacey.newsbuddy.news.NewsApiService
import com.spacey.newsbuddy.news.NewsDao
import com.spacey.newsbuddy.news.NewsResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class NewsRepository(
    private val newsApiService: NewsApiService,
    private val generativeAiService: GenerativeAiService,
    private val chatAiService: ConversationAiService,
    private val newsDao: NewsDao,
    private val genAiDao: GenAiDao
) {

    private var cacheDate: String by Preference(Preference.CACHE_DATE)
    private var newsPreference: String by Preference(Preference.NEWS_RESPONSE)
    private var aiResponse: String by Preference(Preference.AI_RESPONSE)

    suspend fun getNewsConversation(date: String, forceRefresh: Boolean = false): Result<List<Conversation>> = withContext(Dispatchers.IO) {
        val newsAiSummary = genAiDao.getNewsSummary(date)
        if (!forceRefresh && newsAiSummary.isSuccess) {
            return@withContext newsAiSummary.map { daySummary ->
                daySummary.summary.map {
                    Conversation(it.content, it.link)
                }
            }
        }
        log("Date", "Response for cacheDate: $date")
        val news = getTodaysNews(date, forceRefresh)
        news.safeConvert {
            log("News", "News response: $it")
            generativeAiService.runPrompt(it).map { aiMsg ->
                aiResponse = aiMsg
                cacheDate = date
                parseAiResponse(aiMsg)
            }
        }
    }

    suspend fun startAiChat(yesterday: String, forceRefresh: Boolean = false): Result<Flow<String?>> {
        val news = getTodaysNews(yesterday)
        return news.safeConvert {
            log("News", "News response: $it")
            chatAiService.chat(it)
        }
    }

    suspend fun chatWithAi(prompt: String): Result<Flow<String?>> {
        return chatAiService.chat(prompt)
    }

    suspend fun getTodaysNews(date: String, forceRefresh: Boolean = false): Result<String> = withContext(Dispatchers.IO) {
        try {
            val summary = newsDao.getNewsResponse(date)
            if (!forceRefresh && summary.isSuccess) {
                return@withContext summary
            }

            newsApiService.getTodaysTopHeadlines(date).let {
                if (it.getOrThrow().jsonObject.getValue("totalResults").jsonPrimitive.int == 0) {
                    throw Exception("Empty articles list was returned from news API")
                }
                newsDao.upsert(listOf(NewsResponse(date, it.toString())))
            }
            Result.success(newsPreference)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun parseAiResponse(json: String): List<Conversation> {
        val jsonObject: JsonObject = Json.decodeFromString(json.escapeAiContent())
        return Json.decodeFromJsonElement(jsonObject[GenerativeAiService.NEWS_CURATION]!!)
    }

    private fun String.escapeAiContent(): String {
        return replace("\\$", "$")
    }
}