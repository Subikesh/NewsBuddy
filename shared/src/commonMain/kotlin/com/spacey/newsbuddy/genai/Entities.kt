package com.spacey.newsbuddy.genai

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class NewsSummary(
    val date: String,
    val content: String,
    val link: String?,
    val newsOrder: Int,
    @PrimaryKey(autoGenerate = true) val id: Int = 0
)

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