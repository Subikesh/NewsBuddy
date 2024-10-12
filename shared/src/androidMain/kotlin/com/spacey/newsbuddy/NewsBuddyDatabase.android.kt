package com.spacey.newsbuddy

import android.content.Context
import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver

fun getNewsBuddyDatabase(context: Context): NewsBuddyDatabase {
    val dbFile = context.getDatabasePath("news_buddy.db")
    return Room.databaseBuilder<NewsBuddyDatabase>(
        context.applicationContext,
        name = dbFile.absolutePath
    )
        .setDriver(BundledSQLiteDriver())
        .build()
}