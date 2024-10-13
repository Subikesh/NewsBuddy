package com.spacey.newsbuddy.news

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface NewsDao {
    @Query("SELECT content FROM NewsResponse WHERE date = :date")
    fun getNewsResponse(date: String): Result<String>

    @Upsert
    fun upsert(news: List<NewsResponse>)
}

@Entity
data class NewsResponse(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @PrimaryKey val date: String,
    val content: String
)

