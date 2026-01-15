package com.spacey.newsbuddy.network

import com.spacey.newsbuddy.android.BuildConfig
import com.spacey.newsbuddy.news.NewsTokenFetcher

class NewsApiTokenFetcher : NewsTokenFetcher {
    override fun get(): String {
        return BuildConfig.NEWS_API_KEY
    }
}