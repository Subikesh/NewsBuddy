package com.spacey.newsbuddy.news

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Upsert
import com.spacey.newsbuddy.genai.NewsResponse

@Dao
interface NewsDao {
    @Query("SELECT * FROM NewsResponse WHERE date = :date")
    fun getNewsResponse(date: String): NewsResponse

    @Upsert
    fun upsert(news: List<NewsResponse>)
}

