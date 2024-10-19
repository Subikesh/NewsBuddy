package com.spacey.newsbuddy

import androidx.room.Database
import androidx.room.RoomDatabase
import com.spacey.newsbuddy.NewsBuddyDatabase.Companion.DATABASE_VERSION
import com.spacey.newsbuddy.genai.ChatBubble
import com.spacey.newsbuddy.genai.GenAiDao
import com.spacey.newsbuddy.genai.NewsSummary
import com.spacey.newsbuddy.news.NewsDao
import com.spacey.newsbuddy.news.NewsResponse

@Database([NewsResponse::class, ChatBubble::class, NewsSummary::class], version = DATABASE_VERSION)
abstract class NewsBuddyDatabase: RoomDatabase() {

    abstract fun getNewsDao(): NewsDao
    abstract fun getGenAiDao(): GenAiDao

    companion object {
        const val DATABASE_VERSION = 1
    }
}