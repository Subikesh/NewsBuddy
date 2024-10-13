package com.spacey.newsbuddy.genai

import androidx.room.Dao
import androidx.room.Embedded
import androidx.room.Query
import androidx.room.Relation
import androidx.room.Upsert
import com.spacey.newsbuddy.news.NewsResponse

@Dao
interface GenAiDao {
    @Query("SELECT * FROM NewsSummary WHERE date = :date ORDER BY order ASC")
    fun getNewsSummary(date: String): Result<List<NewsSummary>>

    @Upsert
    fun upsert(newsSummaries: List<NewsSummary>)

    // TODO: Select chat for that date, and order by sent timeand parse in repository
    @Query("SELECT * FROM NewsResponse WHERE date = :date ORDER BY order ASC")
    fun getChatWindow(date: String): Result<ChatWindow>

    @Upsert
    fun upsertChatWindow(newsSummaries: List<ChatWindow>)
}

data class ChatWindow(
    @Embedded val dayNews: NewsResponse,
    @Relation(ChatBubble::class, "id", "newsId") val chats: List<ChatBubble>
)
