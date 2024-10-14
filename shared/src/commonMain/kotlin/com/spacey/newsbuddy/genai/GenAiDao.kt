package com.spacey.newsbuddy.genai

import androidx.room.Dao
import androidx.room.Embedded
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Relation
import androidx.room.Upsert
import com.spacey.newsbuddy.news.NewsResponse

@Dao
interface GenAiDao {
    @Query("SELECT * FROM NewsSummary WHERE date = :date ORDER BY order ASC")
    suspend fun getNewsSummary(date: String): Result<List<NewsSummary>>

    @Upsert
    suspend fun upsert(newsSummaries: List<NewsSummary>)

    @Query("SELECT * FROM NewsResponse AS news JOIN ChatBubble as chat ON news.id = chat.news_id WHERE date = :date ORDER BY chat.time ASC")
    suspend fun getChatWindow(date: String): Result<ChatWindow>

    @Insert
    suspend fun insertChat(chatBubble: ChatBubble)
}

data class ChatWindow(
    @Embedded internal val dayNews: NewsResponse,
    @Relation(ChatBubble::class, "id", "newsId") val chats: List<ChatBubble>
)
