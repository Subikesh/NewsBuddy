package com.spacey.newsbuddy.genai

import com.spacey.newsbuddy.common.AiFeaturesDisabled
import com.spacey.newsbuddy.common.Dependencies
import com.spacey.newsbuddy.common.NoInternetException
import kotlinx.coroutines.flow.Flow

abstract class AiService<T> {
    internal abstract suspend fun promptAi(message: String): Result<T>

    suspend fun prompt(message: String, dependencies: Dependencies): Result<T> {
        try {
            if (!dependencies.isInternetConnected()) {
                throw NoInternetException
            }
            if (!dependencies.isAiFeaturesSupported()) {
                throw AiFeaturesDisabled
            }
            return promptAi(message)
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }
}

expect class SummaryAiService() : AiService<String> {
    override suspend fun promptAi(message: String): Result<String>
}

object SummaryConstants {
    const val CONTENT = "content"
    const val NEWS_CURATION = "news_curation"
    const val LINK = "link"
}

expect class ChatAiService(chatHistory: List<ChatBubble>, newsResponseText: String) : AiService<Flow<String?>> {
    override suspend fun promptAi(message: String): Result<Flow<String?>>
}

expect class TitleAiService() : AiService<String> {
    override suspend fun promptAi(message: String): Result<String>
}

const val GEMINI_1_5_PRO = "gemini-1.5-pro"
const val GEMINI_1_5_FLASH = "gemini-1.5-flash-001"
