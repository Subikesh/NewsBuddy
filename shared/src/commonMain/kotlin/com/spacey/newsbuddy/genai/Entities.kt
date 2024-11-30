package com.spacey.newsbuddy.genai

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class NewsResponse(
    val date: String,
    val content: String,
    @PrimaryKey(autoGenerate = true) val id: Int = 0
)

@Entity(primaryKeys = ["newsId", "time"])
data class ChatBubble(
    val newsId: Int,
    val time: Long,
    val type: ChatType,
    val chatText: String,
)

@Entity
data class ChatTitle(
    val newsId: Int,
    val date: String,
    val title: String,
    @PrimaryKey(autoGenerate = true) val id: Int = 0
)

@Entity
data class NewsSummary(
    val date: String,
    val content: String,
    val link: String?,
    val newsOrder: Int,
    @PrimaryKey(autoGenerate = true) val id: Int = 0
)

enum class ChatType {
    USER, AI
}