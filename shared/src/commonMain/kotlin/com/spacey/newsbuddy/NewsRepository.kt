package com.spacey.newsbuddy

import com.spacey.newsbuddy.genai.ChatBubble
import com.spacey.newsbuddy.genai.ChatType
import com.spacey.newsbuddy.genai.ChatWindow
import com.spacey.newsbuddy.genai.Conversation
import com.spacey.newsbuddy.genai.ConversationAiService
import com.spacey.newsbuddy.genai.GenAiDao
import com.spacey.newsbuddy.genai.GenerativeAiService
import com.spacey.newsbuddy.genai.NewsSummary
import com.spacey.newsbuddy.genai.safeGetChatWindow
import com.spacey.newsbuddy.news.NewsApiService
import com.spacey.newsbuddy.news.NewsDao
import com.spacey.newsbuddy.news.NewsResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
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

    suspend fun getNewsConversation(date: String, forceRefresh: Boolean = false): Result<List<Conversation>> = withContext(Dispatchers.IO) {
        val newsAiSummary = kotlin.runCatching { genAiDao.getNewsSummary(date) }
        if (!forceRefresh && newsAiSummary.isSuccess) {
            return@withContext newsAiSummary.map { daySummary ->
                daySummary.map {
                    Conversation(it.content, it.link)
                }
            }
        }
        // Cache not found
        log("Date", "Response for cacheDate: $date")
        val news = getTodaysNews(date, forceRefresh)
        news.safeConvert {
            log("News", "News response: $it")
            var i = 0
            generativeAiService.runPrompt(it.content).map { aiMsg ->
//                aiResponse = aiMsg
//                cacheDate = date
                parseAiResponse(aiMsg).also { convoList ->
                    genAiDao.upsert(convoList.map { NewsSummary(date, it.content, it.link, i++) })
                }
            }
        }
    }

    suspend fun startAiChat(date: String): Result<ChatWindow> {
        val chatWindow = genAiDao.safeGetChatWindow(date)
        if (chatWindow.isSuccess) {
            return chatWindow
        }

        val news = getTodaysNews(date)
        return news.safeConvert {
            log("News", "News response: $it")
            chatAiService.chat(it.content).safeConvert { aiResponse ->
                val aiResponseStr = aiResponse.foldAsString()
                genAiDao.insertChat(ChatBubble(it.id, getCurrentTime(), ChatType.AI, aiResponseStr))
                genAiDao.safeGetChatWindow(it.date)
            }
        }
    }

    suspend fun chatWithAi(chatWindow: ChatWindow, prompt: String): Result<ChatWindow> {
        genAiDao.insertChat(ChatBubble(chatWindow.dayNews.id, getCurrentTime(), ChatType.USER, prompt))
        return chatAiService.chat(prompt).safeConvert { aiResponse ->
            val aiResponseStr = aiResponse.foldAsString()
            genAiDao.insertChat(ChatBubble(chatWindow.dayNews.id, getCurrentTime(), ChatType.AI, aiResponseStr))
            genAiDao.safeGetChatWindow(chatWindow.dayNews.date)
        }
    }

    suspend fun getTodaysNews(date: String, forceRefresh: Boolean = false): Result<NewsResponse> = withContext(Dispatchers.IO) {
        try {
            val summary = runCatching { newsDao.getNewsResponse(date) }
            if (!forceRefresh && summary.isSuccess) {
                return@withContext summary
            }
            newsApiService.getTodaysTopHeadlines(date).let {
                if (it.getOrThrow().jsonObject.getValue("totalResults").jsonPrimitive.int == 0) {
                    throw Exception("Empty articles list was returned from news API")
                }
                newsDao.upsert(listOf(NewsResponse(date, it.toString())))
            }
            kotlin.runCatching { newsDao.getNewsResponse(date) }
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