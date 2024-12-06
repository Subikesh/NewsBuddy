package com.spacey.newsbuddy.persistance

import androidx.room.Database
import androidx.room.RoomDatabase
import com.spacey.newsbuddy.datasync.SyncDao
import com.spacey.newsbuddy.datasync.SyncEntry
import com.spacey.newsbuddy.genai.BuddyChatDao
import com.spacey.newsbuddy.genai.ChatBubble
import com.spacey.newsbuddy.genai.ChatTitle
import com.spacey.newsbuddy.genai.NewsResponse
import com.spacey.newsbuddy.genai.NewsSummary
import com.spacey.newsbuddy.genai.SummaryDao
import com.spacey.newsbuddy.genai.SummaryTitle
import com.spacey.newsbuddy.genai.TitleDao
import com.spacey.newsbuddy.news.NewsDao
import com.spacey.newsbuddy.persistance.NewsBuddyDatabase.Companion.DATABASE_VERSION


@Database(
    [NewsResponse::class, ChatBubble::class, NewsSummary::class, SyncEntry::class, ChatTitle::class, SummaryTitle::class],
    version = DATABASE_VERSION
)
abstract class NewsBuddyDatabase : RoomDatabase() {

    abstract fun getNewsDao(): NewsDao
    abstract fun getGenAiDao(): BuddyChatDao
    abstract fun getSummaryDao(): SummaryDao
    abstract fun getSyncDao(): SyncDao
    abstract fun getTitleDao(): TitleDao

    companion object {
        const val DATABASE_VERSION = 3
    }
}