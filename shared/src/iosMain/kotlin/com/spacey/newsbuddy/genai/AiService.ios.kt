package com.spacey.newsbuddy.genai

import com.spacey.newsbuddy.common.Dependencies
import kotlinx.coroutines.flow.Flow

actual class SummaryAiService {
    actual suspend fun prompt(message: String): Result<String> {
        TODO("Not yet implemented")
    }
}

actual class ChatAiService actual constructor(
    chatHistory: List<ChatBubble>,
    newsResponseText: String
) {
    actual suspend fun prompt(message: String): Result<Flow<String?>> {
        TODO("Not yet implemented")
    }
}

actual class TitleAiService {
    actual suspend fun prompt(message: String): Result<String> {
        TODO("Not yet implemented")
    }
}