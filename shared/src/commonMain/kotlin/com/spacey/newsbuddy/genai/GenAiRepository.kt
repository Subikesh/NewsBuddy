package com.spacey.newsbuddy.genai

import com.spacey.newsbuddy.common.Dependencies
import com.spacey.newsbuddy.common.foldAsString
import com.spacey.newsbuddy.common.getCurrentTime
import com.spacey.newsbuddy.common.log
import com.spacey.newsbuddy.common.safeConvert
import com.spacey.newsbuddy.genai.SummaryConstants.CONTENT
import com.spacey.newsbuddy.genai.SummaryConstants.LINK
import com.spacey.newsbuddy.news.NewsRepository
import com.spacey.newsbuddy.settings.SettingsAccessor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement

class GenAiRepository(
    private val newsRepository: NewsRepository,
    private val summaryAiService: SummaryAiService,
    private val buddyChatDao: BuddyChatDao,
    private val newsSummaryDao: SummaryDao,
    private val dependencies: Dependencies
) {

    private lateinit var currentChatAiService: ChatAiService

    suspend fun getRecentChats(offset: Int = 0, limit: Int = 10): List<String> = withContext(Dispatchers.IO) {
        buddyChatDao.getRecentChats(offset, limit)
    }

    suspend fun getRecentSummaries(offset: Int = 0, limit: Int = 10): List<String> = withContext(Dispatchers.IO) {
        if (SettingsAccessor.summaryFeatureEnabled) {
            emptyList()
        } else {
            newsSummaryDao.getRecentSummaries(offset, limit)
        }
    }

    suspend fun getNewsSummary(date: String, forceRefresh: Boolean = false): Result<List<SummaryParagraph>> = withContext(Dispatchers.IO) {
        if (!SettingsAccessor.summaryFeatureEnabled) {
            return@withContext Result.success(emptyList())
        }
        val newsAiSummary = kotlin.runCatching { newsSummaryDao.getNewsSummary(date) }
        if (!forceRefresh && newsAiSummary.isSuccess && newsAiSummary.getOrThrow().isNotEmpty()) {
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
            summaryAiService.prompt(it.content).map { aiMsg ->
                parseAiResponse(aiMsg).also { convoList ->
                    newsSummaryDao.upsert(convoList.map { NewsSummary(date, it.content, it.link, i++) })
                }
            }
        }
    }

    suspend fun startAiChat(date: String): Result<ChatWindow> = withContext(Dispatchers.IO) {
        val chatWindow = buddyChatDao.safeGetChatWindow(date)
        // TODO: Check if chatWindow was error or chatWindow for that date was empty
        if (chatWindow.isSuccess) {
            currentChatAiService = getConversationAiService(chatWindow.getOrThrow())
            return@withContext chatWindow
        }

        val news = newsRepository.getTodaysNews(date)
        news.safeConvert { newsResponse ->
            currentChatAiService = getConversationAiService(ChatWindow(newsResponse, emptyList()))
            log("News", "News response: $newsResponse")
            currentChatAiService.prompt(newsResponse.content).safeConvert { aiResponse ->
                val aiResponseStr = aiResponse.foldAsString()
                buddyChatDao.insert(ChatBubble(newsResponse.id, getCurrentTime(), ChatType.AI, aiResponseStr))
                buddyChatDao.safeGetChatWindow(newsResponse.date)
            }
        }
    }

    fun chatWithAi(chatWindow: ChatWindow, prompt: String): Flow<Result<ChatWindow>> = flow {
        buddyChatDao.insert(ChatBubble(chatWindow.dayNews.id, getCurrentTime(), ChatType.USER, prompt))
        emit(buddyChatDao.safeGetChatWindow(chatWindow.dayNews.date))
        emit(currentChatAiService.prompt(prompt).safeConvert { aiResponse ->
            val aiResponseStr = aiResponse.foldAsString()
            buddyChatDao.insert(ChatBubble(chatWindow.dayNews.id, getCurrentTime(), ChatType.AI, aiResponseStr))
            buddyChatDao.safeGetChatWindow(chatWindow.dayNews.date)
        })
    }.flowOn(Dispatchers.IO)

    private fun getConversationAiService(chatWindow: ChatWindow): ChatAiService {
        return ChatAiService(dependencies, chatWindow.chats, chatWindow.dayNews.content)
    }

    private fun parseAiResponse(json: String): List<SummaryParagraph> {
        val jsonObject: JsonObject = Json.decodeFromString(json.escapeAiContent())
        return Json.decodeFromJsonElement(jsonObject[SummaryConstants.NEWS_CURATION]!!)
    }

    private fun String.escapeAiContent(): String {
        return replace("\\$", "$")
    }
}

@Serializable
data class SummaryParagraph(
    @SerialName(CONTENT) val content: String,
    @SerialName(LINK) val link: String? = null
) {
    val escapedContent: String = content.replace("/[\u2190-\u21FF]|[\u2600-\u26FF]|[\u2700-\u27BF]|[\u3000-\u303F]|[\u1F300-\u1F64F]|[\u1F680-\u1F6FF]/g", "");
}

class AiBusyException(message: String): Exception(message)