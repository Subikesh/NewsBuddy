package com.spacey.newsbuddy

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform