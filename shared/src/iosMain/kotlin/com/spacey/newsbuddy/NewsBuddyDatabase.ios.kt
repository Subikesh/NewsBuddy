package com.spacey.newsbuddy

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import platform.Foundation.NSHomeDirectory

fun getNewsBuddyDatabase(): NewsBuddyDatabase {
    val dbFile = NSHomeDirectory() + "/news_buddy.db"
    return Room.databaseBuilder<NewsBuddyDatabase>(
        name = dbFile,
        factory = { NewsBuddyDatabase::class.instanciateImpl() }
    )
        .setDriver(BundledSQLiteDriver())
        .build()
}