package com.spacey.newsbuddy

import androidx.room.Database
import androidx.room.RoomDatabase
import com.spacey.newsbuddy.NewsBuddyDatabase.Companion.DATABASE_VERSION
import com.spacey.newsbuddy.news.NewsDao
import com.spacey.newsbuddy.news.NewsResponse

@Database([NewsResponse::class], version = DATABASE_VERSION)
abstract class NewsBuddyDatabase: RoomDatabase() {

    abstract fun getNewsDao(): NewsDao

    companion object {
        const val DATABASE_VERSION = 0
    }
}