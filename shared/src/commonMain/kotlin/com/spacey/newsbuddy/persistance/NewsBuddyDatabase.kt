package com.spacey.newsbuddy.persistance

import androidx.room.Database
import androidx.room.RoomDatabase
import com.spacey.newsbuddy.persistance.NewsBuddyDatabase.Companion.DATABASE_VERSION
import com.spacey.newsbuddy.genai.ChatBubble
import com.spacey.newsbuddy.genai.BuddyChatDao
import com.spacey.newsbuddy.genai.NewsSummary
import com.spacey.newsbuddy.genai.SummaryDao
import com.spacey.newsbuddy.news.NewsDao
import com.spacey.newsbuddy.news.NewsResponse

@Database([NewsResponse::class, ChatBubble::class, NewsSummary::class], version = DATABASE_VERSION)
abstract class NewsBuddyDatabase: RoomDatabase() {

    abstract fun getNewsDao(): NewsDao
    abstract fun getGenAiDao(): BuddyChatDao
    abstract fun getSummaryDao(): SummaryDao

    companion object {
        const val DATABASE_VERSION = 1
    }
}