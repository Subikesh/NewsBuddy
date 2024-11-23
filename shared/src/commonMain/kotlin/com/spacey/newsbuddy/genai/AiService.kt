package com.spacey.newsbuddy.genai

import com.spacey.newsbuddy.common.Dependencies
import com.spacey.newsbuddy.news.NewsResponse
import kotlinx.coroutines.flow.Flow

/*interface AiService {
    suspend fun prompt(message: String): Result<String>
}*/

expect class SummaryAiService(dependencies: Dependencies) {
    suspend fun prompt(message: String): Result<String>
}

expect class ChatAiService(dependencies: Dependencies, chatHistory: List<ChatContent>, newsResponse: NewsResponse) {
    suspend fun prompt(message: String): Result<Flow<String?>>
}

const val GEMINI_1_5_PRO = "gemini-1.5-pro"
const val GEMINI_1_5_FLASH = "gemini-1.5-flash-001"

data class ChatContent(val text: String, val role: ChatType)