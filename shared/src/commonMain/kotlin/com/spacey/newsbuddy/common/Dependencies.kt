package com.spacey.newsbuddy.common

import com.spacey.newsbuddy.persistance.AppPreference
import com.spacey.newsbuddy.persistance.NewsBuddyDatabase

interface Dependencies {
    val preference: Lazy<AppPreference>
    val newsBuddyDatabase: Lazy<NewsBuddyDatabase>

    fun getNewsApiToken(): String
    fun getGeminiApiToken(): String
}