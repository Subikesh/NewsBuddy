package com.spacey.newsbuddy.genai

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface GenAiDao {
    @Query("SELECT content FROM NewsSummary WHERE date = :date")
    fun getNewsSummary(date: String): Result<NewsSummary>

    @Upsert
    fun upsert(newsSummary: NewsSummary)
}

@Entity
data class NewsSummary(
    val date: String,
    val summary: List<NewsAiResponse>
)

@Entity
data class NewsAiResponse(
    val content: String,
    val link: String?
)