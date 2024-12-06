package com.spacey.newsbuddy.genai

import androidx.room.Dao
import androidx.room.Embedded
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Relation
import androidx.room.Transaction

@Dao
interface BuddyChatDao {
    @Transaction
    @Query("SELECT * FROM ChatBubble as chat " +
            "LEFT JOIN NewsResponse AS news " +
            "LEFT JOIN ChatTitle AS chatTitle " +
            "ON news.id = chat.newsId and news.id = chatTitle.newsId " +
            "WHERE news.date = :date ORDER BY chat.time ASC")
    suspend fun getChatWindow(date: String): ChatWindow

    @Insert
    suspend fun insert(chatBubble: ChatBubble)

    // TODO: add a title and return that too
    @Query("SELECT ChatTitle.* FROM " +
            "ChatTitle as chatTitle LEFT JOIN NewsResponse as news " +
            "ON news.id = chatTitle.newsId " +
            "GROUP BY news.date ORDER BY news.date DESC LIMIT :offset, :limit")
    suspend fun getRecentChats(offset: Int, limit: Int): List<ChatTitle>
}

data class ChatWindow(
    @Embedded internal val dayNews: NewsResponse,
    @Relation(ChatTitle::class, parentColumn = "id", "newsId")
    private val chatTitle: ChatTitle?,
    @Relation(ChatBubble::class, "id", "newsId")
    val chats: List<ChatBubble>
) {
    val title: String?
        get() = chatTitle?.title
}

suspend fun BuddyChatDao.safeGetChatWindow(date: String): Result<ChatWindow> = runCatching {
    getChatWindow(date)
}