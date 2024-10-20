package com.spacey.newsbuddy.genai

import com.spacey.newsbuddy.common.foldAsString
import com.spacey.newsbuddy.common.getCurrentTime
import com.spacey.newsbuddy.common.log
import com.spacey.newsbuddy.common.safeConvert
import com.spacey.newsbuddy.news.NewsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement

class GenAiRepository(
    private val newsRepository: NewsRepository,
    private val generativeAiService: GenerativeAiService,
    private val chatAiService: ConversationAiService,
    private val genAiDao: GenAiDao
) {

    suspend fun getNewsConversation(date: String, forceRefresh: Boolean = false): Result<List<SummaryParagraph>> = withContext(
        Dispatchers.IO) {
        val newsAiSummary = kotlin.runCatching { genAiDao.getNewsSummary(date) }
        if (!forceRefresh && newsAiSummary.isSuccess) {
            return@withContext newsAiSummary.map { daySummary ->
                daySummary.map {
                    SummaryParagraph(it.content, it.link)
                }
            }
        }
        // Cache not found
        log("Date", "Response for cacheDate: $date")
        val news = newsRepository.getTodaysNews(date, forceRefresh)
        news.safeConvert {
            log("News", "News response: $it")
            var i = 0
            generativeAiService.runPrompt(it.content).map { aiMsg ->
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

        val news = newsRepository.getTodaysNews(date)
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

    private fun parseAiResponse(json: String): List<SummaryParagraph> {
        val jsonObject: JsonObject = Json.decodeFromString(json.escapeAiContent())
        return Json.decodeFromJsonElement(jsonObject[GenerativeAiService.NEWS_CURATION]!!)
    }

    private fun String.escapeAiContent(): String {
        return replace("\\$", "$")
    }
}

@Serializable
data class SummaryParagraph(
    @SerialName(GenerativeAiService.CONTENT) val content: String,
    @SerialName(GenerativeAiService.LINK) val link: String? = null
) {
    val escapedContent: String = content.replace("/[\u2190-\u21FF]|[\u2600-\u26FF]|[\u2700-\u27BF]|[\u3000-\u303F]|[\u1F300-\u1F64F]|[\u1F680-\u1F6FF]/g", "");
}