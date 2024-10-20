package com.spacey.newsbuddy.genai

import androidx.room.Entity
import androidx.room.PrimaryKey

// Chat
@Entity(primaryKeys = ["newsId", "time"])
data class ChatBubble(
    val newsId: Int,
    val time: Long,
    val type: ChatType,
    val chatText: String,
)

enum class ChatType {
    USER, AI
}