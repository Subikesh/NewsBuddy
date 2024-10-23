package com.spacey.newsbuddy.common

import com.spacey.newsbuddy.persistance.AppPreference
import com.spacey.newsbuddy.persistance.NewsBuddyDatabase

interface Dependencies {
    fun getNewsApiToken(): String
    fun getGeminiApiToken(): String

    fun getPreference(): AppPreference
    fun getNewsBuddyDatabase(): NewsBuddyDatabase
}