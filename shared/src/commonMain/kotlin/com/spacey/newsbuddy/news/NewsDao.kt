package com.spacey.newsbuddy.news

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface NewsDao {
    @Query("SELECT * FROM NewsResponse WHERE date = :date")
    fun getNewsResponse(date: String): Result<NewsResponse>

    @Upsert
    fun upsert(news: List<NewsResponse>)
}

@Entity
data class NewsResponse(
    @PrimaryKey val date: String,
    val content: String,
    @PrimaryKey(autoGenerate = true) val id: Int = 0
)

