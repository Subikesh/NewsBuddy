package com.spacey.newsbuddy.genai

import androidx.room.Entity

@Entity
data class NewsSummary(
    val date: String,
    val content: String,
    val link: String?,
    val order: Int
)

// Chat
@Entity
data class ChatBubble(
    val newsId: Int,
    val chatText: String,
    val time: Long,
    val type: ChatType,
)

enum class ChatType {
    USER, AI
}