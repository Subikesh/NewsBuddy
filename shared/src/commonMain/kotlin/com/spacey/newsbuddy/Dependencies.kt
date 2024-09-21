package com.spacey.newsbuddy

interface Dependencies {
    fun getNewsApiToken(): String
    fun getGeminiApiToken(): String
}