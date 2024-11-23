package com.spacey.newsbuddy.genai

import com.spacey.newsbuddy.common.Dependencies
import com.spacey.newsbuddy.news.NewsResponse
import kotlinx.coroutines.flow.Flow

actual class SummaryAiService actual constructor(dependencies: Dependencies) {
    actual suspend fun prompt(message: String): Result<String> {
        TODO("Not yet implemented")
    }
}

actual class ChatAiService actual constructor(
    dependencies: Dependencies,
    chatHistory: List<ChatContent>,
    newsResponse: NewsResponse
) {
    actual suspend fun prompt(message: String): Result<Flow<String?>> {
        TODO("Not yet implemented")
    }
}