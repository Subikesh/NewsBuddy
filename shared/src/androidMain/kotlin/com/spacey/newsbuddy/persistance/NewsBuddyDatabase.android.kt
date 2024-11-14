package com.spacey.newsbuddy.persistance

import android.content.Context
import androidx.room.Room

fun getNewsBuddyDatabase(context: Context): NewsBuddyDatabase {
    val dbFile = context.getDatabasePath("news_buddy.db")
    return Room.databaseBuilder<NewsBuddyDatabase>(
        context.applicationContext,
        name = dbFile.absolutePath
    )
        .fallbackToDestructiveMigration(false)
        .build()
}