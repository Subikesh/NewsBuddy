package com.spacey.newsbuddy

import com.spacey.newsbuddy.base.AppPreference

interface Dependencies {
    fun getNewsApiToken(): String
    fun getGeminiApiToken(): String

    fun getPreference(): AppPreference
}