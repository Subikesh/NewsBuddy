package com.spacey.newsbuddy.genai

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Upsert

interface SummaryDao {
    @Query("SELECT * FROM NewsSummary WHERE date = :date ORDER BY newsOrder ASC")
    suspend fun getNewsSummary(date: String): List<NewsSummary>

    @Upsert
    suspend fun upsert(newsSummaries: List<NewsSummary>)

    @Query("SELECT news.date FROM NewsSummary WHERE date <= :date ORDER BY date DESC LIMIT :offset, :limit")
    suspend fun getRecentSummaries(offset: Int, limit: Int): List<String>
}

@Entity
data class NewsSummary(
    val date: String,
    val content: String,
    val link: String?,
    val newsOrder: Int,
    @PrimaryKey(autoGenerate = true) val id: Int = 0
)