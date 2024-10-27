package com.spacey.newsbuddy.genai

import androidx.room.Dao
import androidx.room.Embedded
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Relation
import androidx.room.Transaction
import com.spacey.newsbuddy.news.NewsResponse

@Dao
interface BuddyChatDao {
    @Transaction
    @Query("SELECT * FROM ChatBubble as chat LEFT JOIN NewsResponse AS news ON news.id = chat.newsId WHERE date = :date ORDER BY chat.time ASC")
    suspend fun getChatWindow(date: String): ChatWindow

    @Insert
    suspend fun insert(chatBubble: ChatBubble)

    // TODO: add a title and return that too
    @Query("SELECT news.date FROM ChatBubble as chat LEFT JOIN NewsResponse as news ON news.id = chat.newsId GROUP BY date ORDER BY date DESC LIMIT :offset, :limit")
    suspend fun getRecentChats(offset: Int, limit: Int): List<String>
}

data class ChatWindow(
    @Embedded internal val dayNews: NewsResponse,
    @Relation(ChatBubble::class, "id", "newsId") val chats: List<ChatBubble>
)

suspend fun BuddyChatDao.safeGetChatWindow(date: String): Result<ChatWindow> = runCatching {
    getChatWindow(date)
}